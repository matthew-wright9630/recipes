package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.models.Recipe;

import java.util.Collections;
import java.util.List;

import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.RecipeIngredientDto;

public class RecipeMapper {

    public static RecipeDto toDto(Recipe recipe) {
        List<RecipeIngredientDto> ingredientDtos = recipe.getRecipeIngredients() == null ? Collections.emptyList() 
            : recipe.getRecipeIngredients().stream()
            .map(ri -> new RecipeIngredientDto(
                ri.getIngredient().getName(),
                ri.getQuantity(),
                ri.getUnit()
            ))
            .toList();

        return new RecipeDto(
            recipe.getName(),
            recipe.getDescription(),
            recipe.getNotes(),
            recipe.getServings(),
            recipe.getPrepTime(),
            recipe.getCookTime(),
            recipe.getActive(),
            recipe.getVersion(),
            ingredientDtos
        );
    }
}
