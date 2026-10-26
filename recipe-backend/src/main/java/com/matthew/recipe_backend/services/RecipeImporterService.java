package com.matthew.recipe_backend.services;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.repositories.RecipeDirectionRepository;
import com.matthew.recipe_backend.repositories.RecipeRepository;

@Service
public class RecipeImporterService {

    private final RecipeRepository recipeRepository;
    private final UserService userService;
    private final RecipeIngredientService recipeIngredientService;

    public RecipeImporterService(RecipeRepository recipeRepository, UserService userService,
            RecipeIngredientService recipeIngredientService) {
        this.recipeRepository = recipeRepository;
        this.userService = userService;
        this.recipeIngredientService = recipeIngredientService;
    }

}
