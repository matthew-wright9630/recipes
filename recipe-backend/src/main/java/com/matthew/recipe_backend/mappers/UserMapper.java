package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.dtos.UserDto;
import com.matthew.recipe_backend.models.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getDisplayUsername(),
                user.getEmail(),
                user.getRole().name(), user.getAvatarUrl());
    }
}
