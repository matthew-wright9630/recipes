package com.matthew.recipe_backend.mappers;

import java.util.List;

import com.matthew.recipe_backend.dtos.CookbookWithRecipesDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.models.Cookbook;

public class CookbookWithRecipesMapper {

    public static CookbookWithRecipesDto toDto(Cookbook cookbook, List<RecipeDto> recipes, Long userId) {
        boolean isOwner = cookbook.getOwnerId().getId() == userId;
        return new CookbookWithRecipesDto(cookbook.getId(), cookbook.getName(), cookbook.getDescription(),
                cookbook.getImageUrl(), cookbook.getUpdatedAt(), isOwner, recipes);
    }
}
