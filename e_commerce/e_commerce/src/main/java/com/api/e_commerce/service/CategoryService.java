package com.api.e_commerce.service;

import com.api.e_commerce.dto.CategoryDTO;
import com.api.e_commerce.dto.CategoryRequest;
import com.api.e_commerce.exception.ResourceNotFoundException;
import com.api.e_commerce.model.Category;
import com.api.e_commerce.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CategoryService { 
    // 1. Inyectamos el Repositorio
    private final CategoryRepository categoryRepository;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    // --- Metodos 

    @Transactional(readOnly = true)
    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(this::mapToDTO) 
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return mapToDTO(category);
    }

    public CategoryDTO createCategory(CategoryRequest categoryRequest) {
        // Mapea del DTO a la Entidad
        Category category = new Category();
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        // Guarda en la BD
        Category savedCategory = categoryRepository.save(category);
        
        // Mapea de la Entidad al DTO para devolver
        return mapToDTO(savedCategory);
    }

    public CategoryDTO updateCategory(Long id, CategoryRequest categoryRequest) {
        // 1. Busca la categoría
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        
        // 2. Actualiza los campos
        category.setName(categoryRequest.getName());
        category.setDescription(categoryRequest.getDescription());

        // 3. Guarda (JPA sabe que es un update porque el objeto ya tiene ID)
        Category updatedCategory = categoryRepository.save(category);

        return mapToDTO(updatedCategory);
    }

    public void deleteCategory(Long id) {
        // 1. Verifica que existe
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        }
        
        // 2. Borra
        // OJO: Si tienes productos en esta categoría, esto podría fallar
        // dependiendo de tu configuración de base de datos (restricción de llave foránea).
        categoryRepository.deleteById(id);
    }

    // --- Metodo Helper Privado ---

    /**
     * Convierte una entidad Category a un CategoryDTO.
     */
    private CategoryDTO mapToDTO(Category category) {
        return new CategoryDTO(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}