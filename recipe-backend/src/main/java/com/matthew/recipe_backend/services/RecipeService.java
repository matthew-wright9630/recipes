package com.matthew.recipe_backend.services;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.repositories.RecipeRepository;

@Service
public class RecipeService {
    
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> findAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> findRecipeById(long id) {
        return recipeRepository.findById(id);
    }
}
