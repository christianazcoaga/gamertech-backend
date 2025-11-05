package com.api.e_commerce.service;

import com.api.e_commerce.dto.ProductDTO;
import com.api.e_commerce.dto.ProductRequest;
import com.api.e_commerce.exception.ResourceNotFoundException;
import com.api.e_commerce.model.Category;
import com.api.e_commerce.model.Product;
import com.api.e_commerce.repository.CategoryRepository;
import com.api.e_commerce.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.categoryRepository = categoryRepository;
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToDTO(product);
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByCategory(String categoryName) { // <-- 4. CORREGIDO
        // Asume que en ProductRepository cambiaste el método a "findByCategory_Name"
        return productRepository.findByCategory_Name(categoryName).stream() // <-- 5. CORREGIDO
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<ProductDTO> searchProducts(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<String> getCategories() { // <-- 6. CORREGIDO
        // Ahora busca en el repositorio de categorías y devuelve solo los nombres
        return categoryRepository.findAll().stream()
                .map(Category::getName)
                .collect(Collectors.toList());
    }
    
    public ProductDTO createProduct(ProductRequest request) {
        // Buscar la categoría
        Category category = categoryRepository.findById(request.getCategoryId()) 
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(category);
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImage(request.getImage());
        
        Product savedProduct = productRepository.save(product);
        return mapToDTO(savedProduct);
    }
    
    public ProductDTO updateProduct(Long id, ProductRequest request) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        // Actualizar categoría si cambió
        if (!product.getCategory().getId().equals(request.getCategoryId())) {
            Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoryId()));
            product.setCategory(category);
        }
        
        // Actualizar campos
        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImage(request.getImage());
        // NO se cambia el usuario - el producto siempre pertenece al creador original
        
        Product updatedProduct = productRepository.save(product);
        return mapToDTO(updatedProduct);
    }
    
    public ProductDTO updateStock(Long id, Integer newStock) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        if (newStock < 0) {
            throw new IllegalArgumentException("El stock no puede ser negativo");
        }
        
        product.setStock(newStock);
        Product updatedProduct = productRepository.save(product);
        return mapToDTO(updatedProduct);
    }
    
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        
        productRepository.deleteById(id);
    }
    
    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getCategory().getName(),
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImage(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}