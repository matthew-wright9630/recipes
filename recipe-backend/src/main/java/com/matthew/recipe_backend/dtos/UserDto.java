package com.matthew.recipe_backend.dtos;

import java.time.LocalDateTime;

public record UserDto(Long id, String username, String email, String userRole, boolean deactivated,
        LocalDateTime createdAt) {

}
