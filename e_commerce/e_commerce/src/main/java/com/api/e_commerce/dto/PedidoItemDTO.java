package com.api.e_commerce.dto;

import java.math.BigDecimal;

public class PedidoItemDTO {
    
    private Long id;
    private Long productoId;
    private String productoNombre;
    private String productoImagen;
    private String productoCategoria;
    private Integer cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal subtotal;
    
    // Constructors
    public PedidoItemDTO() {
    }
    
    public PedidoItemDTO(Long id, Long productoId, String productoNombre, String productoImagen,
                         String productoCategoria, Integer cantidad, 
                         BigDecimal precioUnitario, BigDecimal subtotal) {
        this.id = id;
        this.productoId = productoId;
        this.productoNombre = productoNombre;
        this.productoImagen = productoImagen;
        this.productoCategoria = productoCategoria;
        this.cantidad = cantidad;
        this.precioUnitario = precioUnitario;
        this.subtotal = subtotal;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getProductoId() {
        return productoId;
    }
    
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }
    
    public String getProductoNombre() {
        return productoNombre;
    }
    
    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }
    
    public String getProductoImagen() {
        return productoImagen;
    }
    
    public void setProductoImagen(String productoImagen) {
        this.productoImagen = productoImagen;
    }
    
    public String getProductoCategoria() {
        return productoCategoria;
    }
    
    public void setProductoCategoria(String productoCategoria) {
        this.productoCategoria = productoCategoria;
    }
    
    public Integer getCantidad() {
        return cantidad;
    }
    
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
    
    public BigDecimal getPrecioUnitario() {
        return precioUnitario;
    }
    
    public void setPrecioUnitario(BigDecimal precioUnitario) {
        this.precioUnitario = precioUnitario;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public void setSubtotal(BigDecimal subtotal) {
        this.subtotal = subtotal;
    }
}
