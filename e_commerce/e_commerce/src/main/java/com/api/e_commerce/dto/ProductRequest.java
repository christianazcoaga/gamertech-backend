package com.api.e_commerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ProductRequest {
    
    @NotBlank(message = "El nombre del producto es obligatorio")
    private String name;
    
    // --- CORREGIDO ---
    // Ya no es un String, es el ID de la categoría.
    @NotNull(message = "El ID de la categoría es obligatorio")
    private Long categoryId;
    
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
    
    @NotNull(message = "El precio es obligatorio")
    @Min(value = 0, message = "El precio debe ser mayor o igual a 0")
    private Double price;
    
    @NotNull(message = "El stock es obligatorio")
    @Min(value = 0, message = "El stock debe ser mayor o igual a 0")
    private Integer stock;
    
    private String image;
    
    // userId es opcional - si no se proporciona, se usa el usuario autenticado
    private Long userId;
    
    // Constructors
    public ProductRequest() {
    }
    
    // --- CONSTRUCTOR CORREGIDO ---
    public ProductRequest(String name, Long categoryId, String description, Double price, 
                         Integer stock, String image, Long userId) {
        this.name = name;
        this.categoryId = categoryId; // Corregido
        this.description = description;
        this.price = price;
        this.stock = stock;
        this.image = image;
        this.userId = userId;
    }
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    // --- GETTER Y SETTER CORREGIDOS ---
    public Long getCategoryId() {
        return categoryId;
    }
    
    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Double getPrice() {
        return price;
    }
    
    public void setPrice(Double price) {
        this.price = price;
    }
    
    public Integer getStock() {
        return stock;
    }
    
    public void setStock(Integer stock) {
        this.stock = stock;
    }
    
    public String getImage() {
        return image;
    }
    
    public void setImage(String image) {
        this.image = image;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
}