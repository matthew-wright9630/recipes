package com.matthew.recipe_backend.dtos;

import java.util.List;

public record RecipeDirectionsDto(
                String description,
                Integer stepNumber,
                List<RecipeIngredientDto> recipeIngredients) {
}
