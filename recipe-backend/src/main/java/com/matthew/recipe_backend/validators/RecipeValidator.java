package com.matthew.recipe_backend.validators;

import java.util.ArrayList;
import java.util.List;

import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.models.Recipe;

/**
 * Utility class providing validation logic for {@link Recipe} entities.
 *
 * <p>
 * All methods are static; this class is not intended to be instantiated.
 */
public class RecipeValidator {

	/**
	 * Validates that a recipe meets all requirements before it can be published.
	 *
	 * <p>
	 * A recipe is considered publishable when it has a name, a description,
	 * at least one ingredient, and at least one direction. All violations are
	 * collected before throwing so the caller receives a complete list of errors
	 * in a single pass.
	 *
	 * @param recipe the recipe to validate
	 * @throws IllegalArgumentException if one or more validation rules are
	 *                                  violated,
	 *                                  with each violation joined into a single
	 *                                  message
	 */
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

		// Throw a single exception containing all violations rather than failing on the
		// first
		if (!errors.isEmpty()) {
			throw new IllegalArgumentException(String.join("; ", errors));
		}
	}

	/**
	 * Returns {@code true} if the given string is {@code null} or blank.
	 *
	 * @param value the string to check
	 * @return {@code true} if {@code value} is {@code null} or contains only
	 *         whitespace
	 */
	public static boolean isNullOrEmpty(String value) {
		return value == null || value.isBlank();
	}

	/**
	 * Validates that a status transition between two {@link RecipeStatus} values is
	 * permitted.
	 *
	 * <p>
	 * The allowed transitions are:
	 * <ul>
	 * <li>{@code DRAFT} → {@code PUBLISHED}</li>
	 * <li>{@code PUBLISHED} → {@code ARCHIVED} or {@code REMOVED}</li>
	 * <li>{@code ARCHIVED} → {@code PUBLISHED}</li>
	 * <li>{@code REMOVED} → <em>none</em> (terminal state)</li>
	 * </ul>
	 *
	 * @param current the recipe's current status
	 * @param next    the desired target status
	 * @throws IllegalStateException if the transition from {@code current} to
	 *                               {@code next}
	 *                               is not permitted
	 */
	public static void validateStatusTransition(RecipeStatus current, RecipeStatus next) {
		boolean valid = switch (current) {
			case DRAFT -> next == RecipeStatus.PUBLISHED;
			case PUBLISHED -> next == RecipeStatus.ARCHIVED || next == RecipeStatus.REMOVED;
			case ARCHIVED -> next == RecipeStatus.PUBLISHED;
			// REMOVED is a terminal state — no further transitions are allowed
			case REMOVED -> false;
		};

		if (!valid) {
			throw new IllegalStateException(
					"Invalid status transition: " + current + " -> " + next);
		}
	}

	/**
	 * Validates that a recipe is in {@code DRAFT} status before a destructive
	 * operation.
	 *
	 * <p>
	 * Only draft recipes may be deleted. Published or archived recipes should
	 * instead
	 * be transitioned to {@code REMOVED}, or handled by an administrator.
	 *
	 * @param recipe the recipe to check
	 * @throws IllegalStateException if the recipe is not in {@code DRAFT} status
	 */
	public static void validateDraftStatus(Recipe recipe) {
		if (!recipe.getStatus().equals(RecipeStatus.DRAFT))
			throw new IllegalStateException(
					"Only draft recipes can be deleted. Please either mark this as removed, or contact an administrator for more help.");
	}
}
