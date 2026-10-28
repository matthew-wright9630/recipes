package com.matthew.recipe_backend.dtos;

import java.util.List;

public record CreateRecipeImportRequest(String name, List<UpdateRecipeIngredientsDto> recipeIngredients,
        Integer servings,
        Integer prepTime, Integer cookTime, String originalUrl, String imageUrl) {

}
