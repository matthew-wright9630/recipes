package com.matthew.recipe_backend.dtos;

import java.time.OffsetDateTime;
import java.util.List;

import com.matthew.recipe_backend.enums.RecipeStatus;

public record RecipeDto(Long id, String name, String description, String imageUrl, String notes, Integer servings,
		Integer prepTime,
		Integer cookTime, Integer version, RecipeStatus status,
		List<RecipeDirectionsDto> recipeDirections,
		List<RecipeIngredientDto> recipeIngredients, OffsetDateTime createdAt, Long createdById) {
}
