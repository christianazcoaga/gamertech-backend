package com.api.e_commerce.controller;

import com.api.e_commerce.dto.PedidoDTO;
import com.api.e_commerce.dto.PedidoRequest;
import com.api.e_commerce.model.EstadoPedido;
import com.api.e_commerce.service.PedidoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5137"}) // Configuración CORS por controlador
public class PedidoController {
    
    private final PedidoService pedidoService;
    
    public PedidoController(PedidoService pedidoService) {
        this.pedidoService = pedidoService;
    }
    
    // Obtener todos los pedidos (solo ADMIN)
    @GetMapping
    public ResponseEntity<List<PedidoDTO>> getAllPedidos() {
        List<PedidoDTO> pedidos = pedidoService.getAllPedidos();
        return ResponseEntity.ok(pedidos);
    }
    
    // Obtener mis pedidos (usuario autenticado)
    @GetMapping("/mis-pedidos")
    public ResponseEntity<List<PedidoDTO>> getMisPedidos(Authentication authentication) {
        // Obtener el usuario autenticado
        com.api.e_commerce.model.User user = (com.api.e_commerce.model.User) authentication.getPrincipal();
        Long usuarioId = user.getId();
        
        List<PedidoDTO> pedidos = pedidoService.getPedidosByUsuario(usuarioId);
        return ResponseEntity.ok(pedidos);
    }
    
    // Obtener pedido por ID
    @GetMapping("/{id}")
    public ResponseEntity<PedidoDTO> getPedidoById(@PathVariable Long id) {
        PedidoDTO pedido = pedidoService.getPedidoById(id);
        return ResponseEntity.ok(pedido);
    }
    
    // Obtener pedidos por estado (solo ADMIN)
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<PedidoDTO>> getPedidosByEstado(@PathVariable EstadoPedido estado) {
        List<PedidoDTO> pedidos = pedidoService.getPedidosByEstado(estado);
        return ResponseEntity.ok(pedidos);
    }
    
    // Crear pedido (usuario autenticado)
    @PostMapping
    public ResponseEntity<PedidoDTO> createPedido(@Valid @RequestBody PedidoRequest request,
                                                   Authentication authentication) {
        // Obtener el usuario autenticado
        com.api.e_commerce.model.User user = (com.api.e_commerce.model.User) authentication.getPrincipal();
        Long usuarioId = user.getId();
        
        PedidoDTO nuevoPedido = pedidoService.createPedido(usuarioId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoPedido);
    }
    
    // Actualizar estado del pedido (solo ADMIN)
    @PutMapping("/{id}/estado")
    public ResponseEntity<PedidoDTO> updateEstadoPedido(@PathVariable Long id,
                                                          @RequestParam EstadoPedido estado) {
        PedidoDTO pedidoActualizado = pedidoService.updateEstadoPedido(id, estado);
        return ResponseEntity.ok(pedidoActualizado);
    }
    
    // Cancelar pedido
    @PutMapping("/{id}/cancelar")
    public ResponseEntity<PedidoDTO> cancelarPedido(@PathVariable Long id) {
        PedidoDTO pedidoCancelado = pedidoService.cancelarPedido(id);
        return ResponseEntity.ok(pedidoCancelado);
    }
    
    // Eliminar pedido (solo ADMIN y solo si está cancelado)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePedido(@PathVariable Long id) {
        pedidoService.deletePedido(id);
        return ResponseEntity.noContent().build();
    }
}
