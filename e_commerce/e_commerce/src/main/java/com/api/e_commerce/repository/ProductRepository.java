package com.api.e_commerce.repository;

import com.api.e_commerce.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
// import org.springframework.data.jpa.repository.Query; // Ya no se usa
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    // --- CORREGIDO ---
    // Busca a través del objeto "category" por su campo "name"
    List<Product> findByCategory_Name(String categoryName);
    
    List<Product> findByUserId(Long userId);
    
    List<Product> findByNameContainingIgnoreCase(String name);
    

}