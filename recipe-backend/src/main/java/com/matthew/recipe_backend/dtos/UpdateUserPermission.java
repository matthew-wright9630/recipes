package com.matthew.recipe_backend.dtos;

import com.matthew.recipe_backend.enums.CookbookPermission;

public record UpdateUserPermission(Long userId,
        CookbookPermission permission,
        boolean removed) {

}
