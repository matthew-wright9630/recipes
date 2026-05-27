package com.matthew.recipe_backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeDirection;
import com.matthew.recipe_backend.models.RecipeIngredient;
import com.matthew.recipe_backend.repositories.RecipeIngredientRepository;

@Service
public class RecipeIngredientService {

    private final RecipeIngredientRepository recipeIngredientRepository;

    public RecipeIngredientService(RecipeIngredientRepository recipeIngredientRepository) {
        this.recipeIngredientRepository = recipeIngredientRepository;
    }

    public void computeAndSaveSortOrder(Recipe recipe) {
        List<RecipeIngredient> ingredients = new ArrayList<>(recipe.getRecipeIngredients());
        List<RecipeDirection> directions = new ArrayList<>(recipe.getRecipeDirections());

        List<RecipeIngredient> ordered = new ArrayList<>();

        // Push matched ingredients in step order
        for (RecipeDirection direction : directions) {
            String text = direction.getDescription().toLowerCase();
            for (RecipeIngredient ingredient : ingredients) {
                String name = ingredient.getIngredient().getName().toLowerCase();
                if (text.contains(name) && !ordered.contains(ingredient)) {
                    ordered.add(ingredient);
                }
            }
        }

        // Append any unmatched ingredients to the end
        for (RecipeIngredient ingredient : ingredients) {
            if (!ordered.contains(ingredient)) {
                ordered.add(ingredient);
            }
        }

        // Update sort_order and save
        for (int i = 0; i < ordered.size(); i++) {
            ordered.get(i).setSortOrder(i + 1);
        }

        recipeIngredientRepository.saveAll(ordered);
    }
}
