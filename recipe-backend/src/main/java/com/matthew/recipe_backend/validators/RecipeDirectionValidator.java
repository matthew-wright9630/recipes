package com.matthew.recipe_backend.validators;

import java.util.ArrayList;
import java.util.List;

import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeDirection;

/**
 * Utility class providing validation logic for {@link RecipeDirection}
 * entities.
 *
 * <p>
 * All methods are static; this class is not intended to be instantiated.
 */
public class RecipeDirectionValidator {

    /**
     * Validates the fields of a {@link RecipeDirection} and collects any
     * violations.
     *
     * <p>
     * Checks that the direction has a non-blank description, a positive step
     * number,
     * an associated recipe, and that no other step on the same recipe already
     * occupies
     * the given step number.
     *
     * @param recipeDirection the direction to validate
     */
    public static void validateRecipeDirection(RecipeDirection recipeDirection) {
        List<String> errors = new ArrayList<>();
        if (isNullOrEmpty(recipeDirection.getDescription()))
            errors.add("Step description is required");
        if (stepAlreadyExists(recipeDirection))
            errors.add("Step number already exists");
        if (recipeDirection.getStepNumber() < 1)
            errors.add("Please use a step number as a positive integer");
        if (recipeDirection.getRecipe() == null)
            errors.add("Step can only be added to an existing recipe.");

        if (!errors.isEmpty())
            throw new IllegalArgumentException(String.join("; ", errors));
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
     * Checks whether another step with the same step number already exists on the
     * recipe.
     *
     * <p>
     * The direction being validated is excluded from the check by ID, so that
     * an existing step can be updated without falsely conflicting with itself.
     *
     * @param recipeDirection the direction whose step number is being checked
     * @return {@code true} if a different direction on the same recipe shares the
     *         same step number
     */
    public static boolean stepAlreadyExists(RecipeDirection recipeDirection) {
        Recipe recipe = recipeDirection.getRecipe();
        int stepNumber = recipeDirection.getStepNumber();

        // Exclude the current direction by ID to allow updates without self-conflict,
        // then check if any remaining step shares the same step number
        return recipe.getRecipeDirection().stream().filter(rd -> !rd.getId().equals(recipeDirection.getId()))
                .anyMatch(rd -> rd.getStepNumber().equals(stepNumber));
    }
}
