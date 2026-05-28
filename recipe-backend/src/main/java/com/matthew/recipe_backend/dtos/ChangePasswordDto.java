package com.matthew.recipe_backend.dtos;

public record ChangePasswordDto(
                String currentPassword,
                String newPassword,
                String confirmPassword) {

}
