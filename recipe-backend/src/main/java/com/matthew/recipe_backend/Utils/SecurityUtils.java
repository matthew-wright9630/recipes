package com.matthew.recipe_backend.Utils;

import org.springframework.security.core.context.SecurityContextHolder;

import com.matthew.recipe_backend.exceptions.AccessDeniedException;

public class SecurityUtils {

    public static Long getCurrentUserId() {
        Object principal = SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();

        if (principal instanceof CustomUserDetails user) {
            return user.getId();
        }

        throw new AccessDeniedException("User is not authenticated");
    }
}
