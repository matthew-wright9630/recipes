package com.matthew.recipe_backend.dtos;

import java.time.OffsetDateTime;

public record CookbookDto(Long id, String name, String description, String imageUrl, OffsetDateTime updatedAt) {
}