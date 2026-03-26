package com.matthew.recipe_backend.dtos;

import java.util.List;

public record RecipeDto(String name, String description, String notes, Integer servings, Integer prepTime,
		Integer cookTime, Boolean active, Integer version, Boolean published,
		List<RecipeDirectionsDto> recipeDirections,
		List<RecipeIngredientDto> recipeIngredients) {
}
