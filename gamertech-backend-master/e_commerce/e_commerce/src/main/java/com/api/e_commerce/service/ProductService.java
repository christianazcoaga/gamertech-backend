package com.api.e_commerce.service;

import com.api.e_commerce.dto.ProductDTO;
import com.api.e_commerce.dto.ProductRequest;
import com.api.e_commerce.exception.ResourceNotFoundException;
import com.api.e_commerce.model.Category; // <-- IMPORTANTE
import com.api.e_commerce.model.Product;
import com.api.e_commerce.model.User;
import com.api.e_commerce.repository.CategoryRepository; // <-- IMPORTANTE
import com.api.e_commerce.repository.ProductRepository;
import com.api.e_commerce.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class ProductService {
    
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository; // <-- 1. AÑADIDO

    @Autowired
    public ProductService(ProductRepository productRepository, UserRepository userRepository, CategoryRepository categoryRepository) { // <-- 2. AÑADIDO
        this.productRepository = productRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository; // <-- 3. AÑADIDO
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
        // Obtener el usuario: si viene en el request, usarlo; si no, usar el usuario autenticado
        User user;
        
        if (request.getUserId() != null) {
            // Si se proporciona userId (por ejemplo, un ADMIN creando para otro usuario)
            user = userRepository.findById(request.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con id: " + request.getUserId()));
        } else {
            // Si no se proporciona, usar el usuario autenticado
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            
            if (authentication == null || !authentication.isAuthenticated()) {
                throw new AccessDeniedException("Debes estar autenticado para crear un producto");
            }
            
            // Obtener el username del UserDetails y buscar el usuario en la BD
            String username = authentication.getName();
            user = userRepository.findByEmail(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Usuario autenticado no encontrado"));
        }
        
        // --- 7. CORREGIDO ---
        // Asumimos que "ProductRequest" ahora tiene un campo "getCategoryId()" que devuelve un Long
        Category category = categoryRepository.findById(request.getCategoryId()) 
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + request.getCategoryId()));

        Product product = new Product();
        product.setName(request.getName());
        product.setCategory(category); // <-- Se pasa el objeto Categoría, no un String
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setImage(request.getImage());
        product.setUser(user);
        
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
    
    @Transactional(readOnly = true)
    public List<ProductDTO> getProductsByUserId(Long userId) {
        // Verificar que el usuario existe
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("User not found with id: " + userId);
        }
        
        return productRepository.findByUserId(userId).stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }
    
    private ProductDTO mapToDTO(Product product) {
        return new ProductDTO(
                product.getId(),
                product.getName(),
                product.getCategory().getName(), // <-- 9. CORREGIDO (Obtiene el nombre)
                product.getDescription(),
                product.getPrice(),
                product.getStock(),
                product.getImage(),
                product.getUser().getId(),
                product.getUser().getUsername(), // <-- 10. CORREGIDO (Asumo que es getUsername())
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}