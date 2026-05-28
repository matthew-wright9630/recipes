package com.matthew.recipe_backend.dtos;

public record AuthResponseDto(String accessToken, String refreshToken, String email, String username, String role) {

}
