package com.matthew.recipe_backend.dtos;

import java.util.List;

public record RecipeImportResponse(String name, List<RecipeIngredientDto> recipeIngredients, Integer servings,
                Integer prepTime, Integer cookTime, String originalUrl) {

}
