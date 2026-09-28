package com.matthew.recipe_backend.dtos;

import java.util.List;

public record RecipeNotesDto(
        String description,
        Integer stepNumber) {
}
