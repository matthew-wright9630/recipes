package com.matthew.recipe_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.services.RecipeService;

import java.util.List;
import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;



@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;

    public RecipeController(RecipeService recipeService) {
        this.recipeService = recipeService;
    }
    
    @GetMapping
    public ResponseEntity<List<RecipeDto>> getAllRecipes() {
        List<RecipeDto> recipes = recipeService.findAllRecipes();
        return ResponseEntity.ok(recipes);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<RecipeDto> getRecipeById(@PathVariable long id) {
        RecipeDto recipe = recipeService.findRecipeById(id);
        return ResponseEntity.ok(recipe);
    }
    
    // @PostMapping
    // public ResponseEntity<RecipeDto> postRecipe(@RequestBody String entity) {
    //     //TODO: process POST request
        
    //     return entity;
    // }
    
}
