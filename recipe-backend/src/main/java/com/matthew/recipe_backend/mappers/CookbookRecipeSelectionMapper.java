package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.dtos.CookbookRecipeSelectionDto;
import com.matthew.recipe_backend.models.Cookbook;

public class CookbookRecipeSelectionMapper {

    public static CookbookRecipeSelectionDto toDto(Cookbook cookbook, boolean containsRecipe) {
        return new CookbookRecipeSelectionDto(
                cookbook.getId(), cookbook.getName(), containsRecipe);
    }
}
