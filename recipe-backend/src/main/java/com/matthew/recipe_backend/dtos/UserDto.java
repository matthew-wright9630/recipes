package com.matthew.recipe_backend.dtos;

public record UserDto(Long id, String username, String email, String userRole, String avatarUrl) {

}
