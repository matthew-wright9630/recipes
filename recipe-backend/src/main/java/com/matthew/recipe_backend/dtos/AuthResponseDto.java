package com.matthew.recipe_backend.dtos;

public record AuthResponseDto(Long id, String accessToken, String refreshToken, String email, String username,
        String role) {

}
