package com.api.e_commerce.model;

public enum EstadoPedido {
    PENDIENTE,      // Pedido creado, esperando procesamiento
    CONFIRMADO,     // Pedido confirmado, en preparación
    ENVIADO,        // Pedido enviado al cliente
    ENTREGADO,      // Pedido entregado exitosamente
    CANCELADO       // Pedido cancelado
}
