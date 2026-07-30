package com.matthew.recipe_backend.validators;

import java.util.List;

import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.repositories.CookbookAccessRepository;

public class CookbookValidator {

    public static void assertUserOwnsCookbook(
            CookbookAccessRepository cookbookAccessRepository,
            Long cookbookId,
            Long userId) {

        boolean isOwner = cookbookAccessRepository
                .existsByCookbookIdAndUserIdAndPermissionIn(
                        cookbookId,
                        userId,
                        List.of(CookbookPermission.OWNER));

        if (!isOwner) {
            throw new IllegalStateException("User does not own this cookbook");
        }
    }

    public static void assertUserHasAccessToCookbook(CookbookAccessRepository cookbookAccessRepository,
            Long cookbookId,
            Long userId) {

        boolean hasAccess = cookbookAccessRepository
                .existsByCookbookIdAndUserIdAndPermissionIn(
                        cookbookId,
                        userId,
                        List.of(
                                CookbookPermission.READ,
                                CookbookPermission.READ_WRITE,
                                CookbookPermission.OWNER));

        if (!hasAccess) {
            throw new IllegalStateException("User does not have access this cookbook");
        }
    }
}
