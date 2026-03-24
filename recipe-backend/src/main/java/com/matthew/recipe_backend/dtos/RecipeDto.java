package com.matthew.recipe_backend.dtos;

import java.util.List;

public record RecipeDto(String name, String description, String notes, int servings, int prepTime, int cookTime,
		boolean deleted, int version, List<RecipeIngredientDto> recipeIngredients) {

}
