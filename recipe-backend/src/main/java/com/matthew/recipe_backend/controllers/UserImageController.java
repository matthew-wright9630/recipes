package com.matthew.recipe_backend.controllers;

import java.io.IOException;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.services.ImageService;

@RestController
@RequestMapping("/api/images")
public class UserImageController {

    private final ImageService imageService;

    public UserImageController(ImageService imageService) {
        this.imageService = imageService;
    }

    @GetMapping("/my-images")
    public ResponseEntity<List<String>> getMyImages(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(imageService.getImagesByUser(user));
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User user) throws IOException {
        String baseKey = imageService.processAndUploadImage(file, user);
        return ResponseEntity.ok(baseKey);
    }
}