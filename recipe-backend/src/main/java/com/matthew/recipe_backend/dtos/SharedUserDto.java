package com.matthew.recipe_backend.dtos;

import com.matthew.recipe_backend.enums.CookbookPermission;

public record SharedUserDto(Long userId, String username, String avatarUrl, CookbookPermission permission) {
}
