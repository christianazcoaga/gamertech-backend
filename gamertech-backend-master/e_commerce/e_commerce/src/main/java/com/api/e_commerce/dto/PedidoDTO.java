package com.api.e_commerce.dto;

import com.api.e_commerce.model.EstadoPedido;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class PedidoDTO {
    
    private Long id;
    private Long usuarioId;
    private String usuarioNombre;
    private LocalDateTime fechaPedido;
    private EstadoPedido estado;
    private BigDecimal total;
    private String direccionEnvio;
    private String telefonoContacto;
    private String notas;
    private List<PedidoItemDTO> items;
    private LocalDateTime fechaActualizacion;
    
    // Constructors
    public PedidoDTO() {
    }
    
    public PedidoDTO(Long id, Long usuarioId, String usuarioNombre, LocalDateTime fechaPedido, 
                     EstadoPedido estado, BigDecimal total, String direccionEnvio, 
                     String telefonoContacto, String notas, List<PedidoItemDTO> items, 
                     LocalDateTime fechaActualizacion) {
        this.id = id;
        this.usuarioId = usuarioId;
        this.usuarioNombre = usuarioNombre;
        this.fechaPedido = fechaPedido;
        this.estado = estado;
        this.total = total;
        this.direccionEnvio = direccionEnvio;
        this.telefonoContacto = telefonoContacto;
        this.notas = notas;
        this.items = items;
        this.fechaActualizacion = fechaActualizacion;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUsuarioId() {
        return usuarioId;
    }
    
    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }
    
    public String getUsuarioNombre() {
        return usuarioNombre;
    }
    
    public void setUsuarioNombre(String usuarioNombre) {
        this.usuarioNombre = usuarioNombre;
    }
    
    public LocalDateTime getFechaPedido() {
        return fechaPedido;
    }
    
    public void setFechaPedido(LocalDateTime fechaPedido) {
        this.fechaPedido = fechaPedido;
    }
    
    public EstadoPedido getEstado() {
        return estado;
    }
    
    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public void setTotal(BigDecimal total) {
        this.total = total;
    }
    
    public String getDireccionEnvio() {
        return direccionEnvio;
    }
    
    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }
    
    public String getTelefonoContacto() {
        return telefonoContacto;
    }
    
    public void setTelefonoContacto(String telefonoContacto) {
        this.telefonoContacto = telefonoContacto;
    }
    
    public String getNotas() {
        return notas;
    }
    
    public void setNotas(String notas) {
        this.notas = notas;
    }
    
    public List<PedidoItemDTO> getItems() {
        return items;
    }
    
    public void setItems(List<PedidoItemDTO> items) {
        this.items = items;
    }
    
    public LocalDateTime getFechaActualizacion() {
        return fechaActualizacion;
    }
    
    public void setFechaActualizacion(LocalDateTime fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }
}
