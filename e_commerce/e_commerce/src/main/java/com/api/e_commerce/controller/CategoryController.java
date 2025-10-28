package com.api.e_commerce.controller;

import com.api.e_commerce.dto.CategoryDTO;
import com.api.e_commerce.dto.CategoryRequest;
import com.api.e_commerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:5137"}) // Configuración CORS por controlador
public class CategoryController {

    private final CategoryService categoryService;

    @Autowired
    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    // --- Endpoint 1: Obtener TODAS las categorías ---
    // GET /api/categories
    @GetMapping
    public ResponseEntity<List<CategoryDTO>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    // --- Endpoint 2: Obtener UNA categoría por ID ---
    // GET /api/categories/5
    @GetMapping("/{id}")
    public ResponseEntity<CategoryDTO> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(category);
    }

    // --- Endpoint 3: Crear una NUEVA categoría ---
    // POST /api/categories
    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryDTO newCategory = categoryService.createCategory(categoryRequest);
        // Devuelve 201 Created (el estándar para POST)
        return new ResponseEntity<>(newCategory, HttpStatus.CREATED);
    }

    // --- Endpoint 4: Actualizar una categoría existente ---
    // PUT /api/categories/5
    @PutMapping("/{id}")
    public ResponseEntity<CategoryDTO> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest categoryRequest) {
        CategoryDTO updatedCategory = categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(updatedCategory);
    }

    // --- Endpoint 5: Borrar una categoría ---
    // DELETE /api/categories/5
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        // Devuelve 204 No Content (el estándar para DELETE)
        return ResponseEntity.noContent().build();
    }
}