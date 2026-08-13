package com.matthew.recipe_backend.dtos;

import com.matthew.recipe_backend.enums.CookbookType;

public record CookbookRecipeSelectionDto(Long id, String name, boolean containsRecipe, CookbookType type) {

}
