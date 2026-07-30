package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.models.Cookbook;

public class CookbookMapper {

    public static CookbookDto toDto(Cookbook cookbook) {
        return new CookbookDto(
                cookbook.getId(), cookbook.getName(), cookbook.getDescription(), cookbook.getImageUrl(),
                cookbook.getUpdatedAt());
    }
}
