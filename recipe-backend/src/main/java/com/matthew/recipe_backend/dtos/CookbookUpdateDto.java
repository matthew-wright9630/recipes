package com.matthew.recipe_backend.dtos;

public record CookbookUpdateDto(
        Long cookbookId,
        boolean shouldContainRecipe) {

}
