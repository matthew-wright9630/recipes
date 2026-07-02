package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeIngredient;

import java.util.Collections;
import java.util.List;

import com.matthew.recipe_backend.dtos.RecipeDirectionsDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.RecipeIngredientDto;

public class RecipeMapper {

	public static RecipeDto toDto(Recipe recipe) {
		List<RecipeIngredientDto> ingredientDtos = recipe.getRecipeIngredients() == null ? Collections.emptyList()
				: recipe.getRecipeIngredients()
						.stream()
						.map(ri -> new RecipeIngredientDto(ri.getIngredient().getName(),
								ri.getQuantity(),
								ri.getUnit(),
								ri.getNotes(),
								ri.getSortOrder()))
						.toList();

		List<RecipeDirectionsDto> directionsDtos = recipe.getRecipeDirections() == null ? Collections.emptyList()
				: recipe.getRecipeDirections().stream()
						.map(rd -> {
							return new RecipeDirectionsDto(rd.getDescription(), rd.getStepNumber());
						})
						.toList();

		return new RecipeDto(recipe.getId(), recipe.getName(), recipe.getDescription(), recipe.getImageUrl(),
				recipe.getNotes(),
				recipe.getServings(),
				recipe.getPrepTime(), recipe.getCookTime(), recipe.getVersion(),
				recipe.getStatus(), directionsDtos, ingredientDtos, recipe.getCreatedAt(),
				recipe.getCreatedBy().getId());
	}

	private static RecipeIngredientDto toIngredientDto(RecipeIngredient ri) {
		if (ri == null)
			return null;
		return new RecipeIngredientDto(
				ri.getIngredient().getName(),
				ri.getQuantity(),
				ri.getUnit(),
				ri.getNotes(),
				ri.getSortOrder());
	}
}
