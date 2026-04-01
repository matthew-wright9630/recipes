package com.matthew.recipe_backend.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.CreateDirectionDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.ReorderDirectionDto;
import com.matthew.recipe_backend.services.RecipeDirectionService;
import com.matthew.recipe_backend.dtos.UpdateDirectionDto;

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

    @PutMapping("/{directionId}")
    public ResponseEntity<RecipeDto> updateRecipeDirection(@PathVariable Long recipeId, @PathVariable Long directionId,
            @RequestBody UpdateDirectionDto description) {

        RecipeDto recipe = recipeDirectionService.editRecipeDirection(recipeId, directionId, description);

        return ResponseEntity.ok(recipe);
    }

    @DeleteMapping("/{directionId}")
    public ResponseEntity<Object> deleteRecipeDirection(@PathVariable Long recipeId, @PathVariable Long directionId) {
        recipeDirectionService.deleteRecipeDirection(recipeId, directionId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/reorder")
    public ResponseEntity<RecipeDto> reorderDirections(@PathVariable Long recipeId,
            @RequestBody List<ReorderDirectionDto> request) {
        RecipeDto recipe = recipeDirectionService.reorderDirections(recipeId, request);
        return ResponseEntity.ok(recipe);
    }
}
