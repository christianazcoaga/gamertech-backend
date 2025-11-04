package com.api.e_commerce.repository;

import com.api.e_commerce.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    // Este es un método personalizado que te permite buscar 
    // una categoría por su nombre, por si lo necesitas más adelante.
    Optional<Category> findByName(String name);
}