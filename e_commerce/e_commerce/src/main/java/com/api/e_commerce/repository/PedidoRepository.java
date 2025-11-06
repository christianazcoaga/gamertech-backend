package com.api.e_commerce.repository;

import com.api.e_commerce.model.EstadoPedido;
import com.api.e_commerce.model.Pedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, Long> {
    
    // Buscar pedidos por usuario
    List<Pedido> findByUsuarioId(Long usuarioId);
    
    // Buscar pedidos por estado
    List<Pedido> findByEstado(EstadoPedido estado);
    
    // Buscar pedidos por usuario y estado
    List<Pedido> findByUsuarioIdAndEstado(Long usuarioId, EstadoPedido estado);
    
    // Buscar pedidos por usuario ordenados por fecha descendente
    List<Pedido> findByUsuarioIdOrderByFechaPedidoDesc(Long usuarioId);
}
