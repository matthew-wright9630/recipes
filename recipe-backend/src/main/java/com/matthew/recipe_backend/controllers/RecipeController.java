package com.matthew.recipe_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.responseDtos.RecipeResponse;
import com.matthew.recipe_backend.services.RecipeService;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping
    public ResponseEntity<List<RecipeResponse>> getAllRecipes() {
        List<Recipe> recipes = recipeService.findAllRecipes();
        List<RecipeResponse> RecipeResponses = recipes.stream().map(RecipeMapper::toDto).toList();
        return ResponseEntity.ok(RecipeResponses);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecipeResponse> getRecipeById(@PathVariable long id) {
        Optional<Recipe> foundRecipe = recipeService.findRecipeById(id);
        return foundRecipe.map(r -> ResponseEntity.ok(RecipeMapper.toDto(r))).orElseGet(() -> ResponseEntity.notFound().build());
    }
    
    
}
