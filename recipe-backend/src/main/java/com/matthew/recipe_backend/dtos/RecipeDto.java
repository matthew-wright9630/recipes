package com.matthew.recipe_backend.dtos;

import java.util.List;

import com.matthew.recipe_backend.enums.RecipeStatus;

public record RecipeDto(String name, String description, String notes, Integer servings, Integer prepTime,
		Integer cookTime, Integer version, RecipeStatus status,
		List<RecipeDirectionsDto> recipeDirections,
		List<RecipeIngredientDto> recipeIngredients) {
}
