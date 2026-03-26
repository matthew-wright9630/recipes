package com.matthew.recipe_backend.validators;

import java.util.ArrayList;
import java.util.List;

import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.models.Recipe;

public class RecipeValidator {

	public static void validateRecipePublish(Recipe recipe) {
		List<String> errors = new ArrayList<>();
		if (isNullOrEmpty(recipe.getName()))
			errors.add("Name is required");
		if (isNullOrEmpty(recipe.getDescription()))
			errors.add("Description is required");
		if (recipe.getRecipeIngredients() == null || recipe.getRecipeIngredients().isEmpty())
			errors.add("At least one ingredient is required");
		if (recipe.getRecipeDirection() == null || recipe.getRecipeDirection().isEmpty())
			errors.add("At least one direction is required");

		if (!errors.isEmpty()) {
			throw new IllegalStateException(String.join("; ", errors));
		}
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isBlank();
	}

	public static void validateStatusTransition(RecipeStatus current, RecipeStatus next) {
		boolean valid = switch (current) {
			case DRAFT -> next == RecipeStatus.PUBLISHED;
			case PUBLISHED -> next == RecipeStatus.ARCHIVED || next == RecipeStatus.REMOVED;
			case ARCHIVED -> next == RecipeStatus.PUBLISHED;
			case REMOVED -> false;
		};

		if (!valid) {
			throw new IllegalStateException(
					"Invalid status transition: " + current + " -> " + next);
		}
	}

	public static void validateDraftStatus(Recipe recipe) {
		if (!recipe.getStatus().equals(RecipeStatus.DRAFT))
			throw new IllegalStateException(
					"Only draft recipes can be deleted. Please either mark this as removed, or contact an administrator for more help.");
	}

}
