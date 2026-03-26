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
						.map(ri -> new RecipeIngredientDto(ri.getIngredient().getName(), ri.getQuantity(),
								ri.getUnit()))
						.toList();

		List<RecipeDirectionsDto> directionsDtos = recipe.getRecipeDirection() == null ? Collections.emptyList()
				: recipe.getRecipeDirection().stream()
						.map(rd -> {
							System.out.println(rd.getRecipeIngredients());
							List<RecipeIngredientDto> ingredientDto = rd.getRecipeIngredients() == null
									? Collections.emptyList()
									: rd.getRecipeIngredients().stream()
											.map(ri -> toIngredientDto(ri))
											.toList();
							return new RecipeDirectionsDto(rd.getDescription(), rd.getStepNumber(), ingredientDto);
						})
						.toList();

		return new RecipeDto(recipe.getName(), recipe.getDescription(), recipe.getNotes(), recipe.getServings(),
				recipe.getPrepTime(), recipe.getCookTime(), recipe.getActive(), recipe.getVersion(),
				recipe.getPublished(), directionsDtos, ingredientDtos);
	}

	private static RecipeIngredientDto toIngredientDto(RecipeIngredient ri) {
		if (ri == null)
			return null;
		return new RecipeIngredientDto(
				ri.getIngredient().getName(),
				ri.getQuantity(),
				ri.getUnit());
	}
}
