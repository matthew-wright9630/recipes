package com.matthew.recipe_backend.controllers;

import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.matthew.recipe_backend.services.RecipeImporterService;

@RestController
@RequestMapping("/api/recipe-importer")
public class RecipeImporterController {

    private final RecipeImporterService recipeImporterService;

    public RecipeImporterController(RecipeImporterService recipeImporterService) {
        this.recipeImporterService = recipeImporterService;
    }

    @GetMapping("/test")
    public ResponseEntity<String> fetchRecipePage() throws IOException {
        String html = recipeImporterService.fetchPage(
                "https://wrightrecipes.com/recipe/24");

        return ResponseEntity.ok(html);
    }
}
