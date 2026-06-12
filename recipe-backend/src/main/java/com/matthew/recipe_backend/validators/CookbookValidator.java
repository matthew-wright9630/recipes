package com.matthew.recipe_backend.validators;

import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.repositories.CookbookAccessRepository;

public class CookbookValidator {

    public static void assertUserOwnsCookbook(
            CookbookAccessRepository cookbookAccessRepository,
            Long cookbookId,
            Long userId) {

        boolean isOwner = cookbookAccessRepository
                .existsByCookbookIdAndUserIdAndPermission(
                        cookbookId,
                        userId,
                        CookbookPermission.OWNER);

        if (!isOwner) {
            throw new IllegalStateException("User does not own this cookbook");
        }
    }
}
