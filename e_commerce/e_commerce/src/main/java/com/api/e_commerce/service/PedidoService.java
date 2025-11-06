package com.api.e_commerce.service;

import com.api.e_commerce.dto.PedidoDTO;
import com.api.e_commerce.dto.PedidoItemDTO;
import com.api.e_commerce.dto.PedidoItemRequest;
import com.api.e_commerce.dto.PedidoRequest;
import com.api.e_commerce.exception.ResourceNotFoundException;
import com.api.e_commerce.model.*;
import com.api.e_commerce.repository.PedidoRepository;
import com.api.e_commerce.repository.ProductRepository;
import com.api.e_commerce.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PedidoService {
    
    private final PedidoRepository pedidoRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    
    public PedidoService(PedidoRepository pedidoRepository, 
                         UserRepository userRepository,
                         ProductRepository productRepository) {
        this.pedidoRepository = pedidoRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
    }
    
    // Obtener todos los pedidos (solo para ADMIN)
    public List<PedidoDTO> getAllPedidos() {
        return pedidoRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener pedido por ID
    public PedidoDTO getPedidoById(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        return mapToDTO(pedido);
    }
    
    // Obtener pedidos de un usuario
    public List<PedidoDTO> getPedidosByUsuario(Long usuarioId) {
        return pedidoRepository.findByUsuarioIdOrderByFechaPedidoDesc(usuarioId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    // Obtener pedidos por estado
    public List<PedidoDTO> getPedidosByEstado(EstadoPedido estado) {
        return pedidoRepository.findByEstado(estado).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    // Crear pedido
    public PedidoDTO createPedido(Long usuarioId, PedidoRequest request) {
        // Validar que el usuario existe
        User usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + usuarioId));
        
        // Crear el pedido
        Pedido pedido = new Pedido(usuario, request.getDireccionEnvio(), request.getTelefonoContacto());
        pedido.setNotas(request.getNotas());
        
        // Agregar items al pedido
        for (PedidoItemRequest itemRequest : request.getItems()) {
            // Buscar producto
            Product producto = productRepository.findById(itemRequest.getProductoId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Producto no encontrado con ID: " + itemRequest.getProductoId()));
            
            // Validar stock
            if (producto.getStock() < itemRequest.getCantidad()) {
                throw new IllegalArgumentException(
                        "Stock insuficiente para el producto: " + producto.getName() + 
                        ". Stock disponible: " + producto.getStock());
            }
            
            // Crear item del pedido
            PedidoItem item = new PedidoItem(
                    pedido, 
                    producto, 
                    itemRequest.getCantidad(), 
                    BigDecimal.valueOf(producto.getPrice())
            );
            
            // Agregar item al pedido
            pedido.addItem(item);
            
            // NO descontamos el stock aquí - se descontará al CONFIRMAR el pedido
        }
        
        // Guardar pedido
        Pedido savedPedido = pedidoRepository.save(pedido);
        return mapToDTO(savedPedido);
    }
    
    // Actualizar estado del pedido
    public PedidoDTO updateEstadoPedido(Long id, EstadoPedido nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        
        EstadoPedido estadoAnterior = pedido.getEstado();
        
        // Validar transición de estado
        if (estadoAnterior == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("No se puede modificar un pedido cancelado");
        }
        
        if (estadoAnterior == EstadoPedido.ENTREGADO && nuevoEstado != EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede modificar un pedido ya entregado");
        }
        
        // Si se intenta cancelar, no permitir cancelar pedidos entregados
        if (nuevoEstado == EstadoPedido.CANCELADO && estadoAnterior == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido ya entregado");
        }
        
        // Si el pedido pasa de PENDIENTE a CONFIRMADO, descontar stock
        if (estadoAnterior == EstadoPedido.PENDIENTE && nuevoEstado == EstadoPedido.CONFIRMADO) {
            for (PedidoItem item : pedido.getItems()) {
                Product producto = item.getProducto();
                
                // Validar stock disponible nuevamente
                if (producto.getStock() < item.getCantidad()) {
                    throw new IllegalArgumentException(
                            "Stock insuficiente para confirmar el pedido. Producto: " + producto.getName() + 
                            ". Stock disponible: " + producto.getStock() + 
                            ". Cantidad solicitada: " + item.getCantidad());
                }
                
                // Descontar stock
                producto.setStock(producto.getStock() - item.getCantidad());
                productRepository.save(producto);
            }
        }
        
        // Si el pedido se cancela desde CONFIRMADO o ENVIADO, devolver el stock
        if (nuevoEstado == EstadoPedido.CANCELADO && 
            (estadoAnterior == EstadoPedido.CONFIRMADO || estadoAnterior == EstadoPedido.ENVIADO)) {
            for (PedidoItem item : pedido.getItems()) {
                Product producto = item.getProducto();
                producto.setStock(producto.getStock() + item.getCantidad());
                productRepository.save(producto);
            }
        }
        
        pedido.setEstado(nuevoEstado);
        Pedido updatedPedido = pedidoRepository.save(pedido);
        return mapToDTO(updatedPedido);
    }
    
    // Cancelar pedido
    public PedidoDTO cancelarPedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        
        // Validar que el pedido puede ser cancelado
        if (pedido.getEstado() == EstadoPedido.ENTREGADO) {
            throw new IllegalArgumentException("No se puede cancelar un pedido ya entregado");
        }
        
        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("El pedido ya está cancelado");
        }
        
        // Solo devolver stock si el pedido estaba CONFIRMADO o ENVIADO (estados donde ya se descontó el stock)
        if (pedido.getEstado() == EstadoPedido.CONFIRMADO || pedido.getEstado() == EstadoPedido.ENVIADO) {
            for (PedidoItem item : pedido.getItems()) {
                Product producto = item.getProducto();
                producto.setStock(producto.getStock() + item.getCantidad());
                productRepository.save(producto);
            }
        }
        
        // Cambiar estado
        pedido.setEstado(EstadoPedido.CANCELADO);
        Pedido canceledPedido = pedidoRepository.save(pedido);
        return mapToDTO(canceledPedido);
    }
    
    // Eliminar pedido (solo si está cancelado)
    public void deletePedido(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con ID: " + id));
        
        if (pedido.getEstado() != EstadoPedido.CANCELADO) {
            throw new IllegalArgumentException("Solo se pueden eliminar pedidos cancelados");
        }
        
        pedidoRepository.deleteById(id);
    }
    
    // Mapear Pedido a PedidoDTO
    private PedidoDTO mapToDTO(Pedido pedido) {
        List<PedidoItemDTO> itemsDTO = pedido.getItems().stream()
                .map(this::mapItemToDTO)
                .collect(Collectors.toList());
        
        return new PedidoDTO(
                pedido.getId(),
                pedido.getUsuario().getId(),
                pedido.getUsuario().getName() + " " + pedido.getUsuario().getApellido(),
                pedido.getFechaPedido(),
                pedido.getEstado(),
                pedido.getTotal(),
                pedido.getDireccionEnvio(),
                pedido.getTelefonoContacto(),
                pedido.getNotas(),
                itemsDTO,
                pedido.getFechaActualizacion()
        );
    }
    
    // Mapear PedidoItem a PedidoItemDTO
    private PedidoItemDTO mapItemToDTO(PedidoItem item) {
        Product producto = item.getProducto();
        return new PedidoItemDTO(
                item.getId(),
                producto.getId(),
                producto.getName(),
                producto.getImage(),
                producto.getCategory().getName(),
                item.getCantidad(),
                item.getPrecioUnitario(),
                item.getSubtotal()
        );
    }
}
