package com.matthew.recipe_backend.validators;

import java.util.ArrayList;
import java.util.List;

import com.matthew.recipe_backend.models.Recipe;

public class RecipeValidator {

	public static List<String> validateRecipePublish(Recipe recipe) {
		List<String> errors = new ArrayList<>();
		if (isNullOrEmpty(recipe.getName()))
			errors.add("Name is required");
		if (isNullOrEmpty(recipe.getDescription()))
			errors.add("Description is required");
		if (recipe.getRecipeIngredients() == null || recipe.getRecipeIngredients().isEmpty())
			errors.add("At least one ingredient is required");
		if (recipe.getRecipeDirection() == null || recipe.getRecipeDirection().isEmpty())
			errors.add("At least one direction is required");
		return errors;
	}

	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isBlank();
	}

}
