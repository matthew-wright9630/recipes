package com.matthew.recipe_backend.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.AuthResponseDto;
import com.matthew.recipe_backend.dtos.LoginRequestDto;
import com.matthew.recipe_backend.dtos.RefreshRequestDto;
import com.matthew.recipe_backend.dtos.RegisterRequestDto;
import com.matthew.recipe_backend.services.AuthService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody RegisterRequestDto request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(request));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDto> refresh(@RequestBody RefreshRequestDto request) {
        return ResponseEntity.ok(authService.refresh(request.refreshToken()));
    }

    // @PostMapping("/logout")
    // public ResponseEntity<Void> logout(@RequestHeader("Authorization") String
    // token) {
    // authService.logout(token);
    // return ResponseEntity.noContent().build();
    // }
}
