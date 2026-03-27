package com.matthew.recipe_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.CreateDirectionDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.services.RecipeDirectionService;

@RestController
@RequestMapping("/api/recipes/{recipeId}/directions")
public class RecipeDirectionController {

    private final RecipeDirectionService recipeDirectionService;

    public RecipeDirectionController(RecipeDirectionService recipeDirectionService) {
        this.recipeDirectionService = recipeDirectionService;
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipeDirection(@PathVariable Long recipeId,
            @RequestBody CreateDirectionDto newDirection) {
        RecipeDto recipe = recipeDirectionService.addRecipeDirection(recipeId, newDirection);
        return ResponseEntity.ok(recipe);
    }
}
