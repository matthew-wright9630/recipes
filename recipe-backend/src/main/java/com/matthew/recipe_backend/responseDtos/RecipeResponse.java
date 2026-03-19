package com.matthew.recipe_backend.responseDtos;

public record RecipeResponse(
    String name,
    String description,
    String notes,
    int servings,
    int prepTime,
    int cookTime,
    boolean deleted,
    int version
) {
    
}
