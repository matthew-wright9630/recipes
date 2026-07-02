package com.matthew.recipe_backend.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.services.RecipeService;

@RestController
@RequestMapping("/api/recipe-likes")
public class RecipeLikeController {

    private final RecipeService recipeService;

    public RecipeLikeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @PostMapping("/{id}/like")
    public ResponseEntity<Void> likeRecipe(@PathVariable Long id, @AuthenticationPrincipal User user) {
        recipeService.likeRecipe(id, user);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}/like")
    public ResponseEntity<Void> unlikeRecipe(@PathVariable Long id, @AuthenticationPrincipal User user) {
        recipeService.unlikeRecipe(id, user);
        return ResponseEntity.ok().build();
    }
}
