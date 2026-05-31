package com.matthew.recipe_backend.mappers;

import com.matthew.recipe_backend.dtos.UserDto;
import com.matthew.recipe_backend.dtos.UserSummaryDto;
import com.matthew.recipe_backend.models.User;

public class UserMapper {

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getDisplayUsername(),
                user.getEmail(),
                user.getRole().name(),
                user.isDeactivated());
    }

    public static UserSummaryDto toSummaryDto(User user) {
        return new UserSummaryDto(
                user.getId(),
                user.getDisplayUsername(),
                user.getRole().name());
    }
}
