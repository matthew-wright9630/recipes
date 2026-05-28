package com.matthew.recipe_backend.controllers;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.ChangePasswordDto;
import com.matthew.recipe_backend.dtos.UserDto;
import com.matthew.recipe_backend.dtos.UserUpdateDto;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.services.UserService;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    @PreAuthorize("hasRole('Admin')")
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userService.getAllUsers(pageable);
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getCurrentUser(user));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> putCurrentUser(@AuthenticationPrincipal User user,
            @RequestBody UserUpdateDto request) {
        return ResponseEntity.ok(userService.updateUser(user, request));
    }

    // @PutMapping("/me/password")
    // public ResponseEntity<Void> changePassword(
    // @AuthenticationPrincipal User user,
    // @RequestBody ChangePasswordDto request) {
    // userService.changePassword(user, request);
    // return ResponseEntity.noContent().build();
    // }

}
