package com.matthew.recipe_backend.dtos;

public record UpdateRecipeDto(
    String name,
    String description,
    String notes,
    int servings,
    int prepTime,
    int cookTime
) {
    
}
