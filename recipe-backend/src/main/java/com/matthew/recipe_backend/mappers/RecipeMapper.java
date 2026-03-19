package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.responseDtos.RecipeResponse;

public class RecipeMapper {
    
    public static RecipeResponse toDto(Recipe recipe) {
        return new RecipeResponse(
            recipe.getName(),
            recipe.getDescription(),
            recipe.getNotes(),
            recipe.getServings(),
            recipe.getPrepTime(),
            recipe.getCookTime(),
            recipe.getDeleted(),
            recipe.getVersion()
        );
    }
}
