package com.matthew.recipe_backend.dtos;

import java.time.OffsetDateTime;
import java.util.List;

import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.enums.CookbookType;

public record CookbookWithRecipesDto(Long id, String name, String description, String imageUrl,
        OffsetDateTime updatedAt, CookbookPermission permission, List<RecipeDto> recipes, CookbookType type,
        Long ownerId, String ownerUsername) {

}
