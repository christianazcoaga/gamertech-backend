package com.api.e_commerce.dto;

import com.api.e_commerce.model.Role;
import com.api.e_commerce.dto.DireccionDTO;
import java.time.LocalDateTime;

public class UserDTO {
    
    private Long id;
    private String username;
    private String email;
    private String name;
    private String apellido;
    private Role role;
    private LocalDateTime createdAt;
    private DireccionDTO direccion;
    
    // Constructors
    public UserDTO() {
    }
    
    public UserDTO(Long id, String username, String email, String name, String apellido, Role role, LocalDateTime createdAt, DireccionDTO direccion) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.name = name;
        this.apellido = apellido;
        this.role = role;
        this.createdAt = createdAt;
        this.direccion = direccion;
    }
    public DireccionDTO getDireccion() {
        return direccion;
    }

    public void setDireccion(DireccionDTO direccion) {
        this.direccion = direccion;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getApellido() {
        return apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    
    public Role getRole() {
        return role;
    }
    
    public void setRole(Role role) {
        this.role = role;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
