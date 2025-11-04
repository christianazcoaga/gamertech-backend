package com.api.e_commerce.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

public class PedidoRequest {
    
    @NotBlank(message = "La dirección de envío es obligatoria")
    @Size(max = 255, message = "La dirección no puede exceder 255 caracteres")
    private String direccionEnvio;
    
    @Size(max = 20, message = "El teléfono no puede exceder 20 caracteres")
    private String telefonoContacto;
    
    @Size(max = 500, message = "Las notas no pueden exceder 500 caracteres")
    private String notas;
    
    @NotEmpty(message = "El pedido debe contener al menos un producto")
    @Valid
    private List<PedidoItemRequest> items;
    
    // Constructors
    public PedidoRequest() {
    }
    
    public PedidoRequest(String direccionEnvio, String telefonoContacto, String notas, List<PedidoItemRequest> items) {
        this.direccionEnvio = direccionEnvio;
        this.telefonoContacto = telefonoContacto;
        this.notas = notas;
        this.items = items;
    }
    
    // Getters and Setters
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
    
    public List<PedidoItemRequest> getItems() {
        return items;
    }
    
    public void setItems(List<PedidoItemRequest> items) {
        this.items = items;
    }
}
