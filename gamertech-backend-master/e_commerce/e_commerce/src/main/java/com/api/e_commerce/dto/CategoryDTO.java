package com.api.e_commerce.dto;

import java.time.LocalDateTime;

public record CategoryDTO(
    Long id,
    String name,
    String description,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
}