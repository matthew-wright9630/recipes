package com.matthew.recipe_backend.dtos;

import java.util.List;

public record UpdateRecipeDto(String name, String description, String imageUrl, String notes,
                int servings, int prepTime, int cookTime, List<UpdateRecipeDirectionsDto> recipeDirections,
                List<UpdateRecipeIngredientsDto> recipeIngredients) {

}
