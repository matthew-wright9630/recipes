package com.matthew.recipe_backend.dtos;

import java.time.OffsetDateTime;
import java.util.List;

public record CookbookWithRecipesDto(Long id, String name, String description, String imageUrl,
        OffsetDateTime updatedAt, List<RecipeDto> recipes) {

}
