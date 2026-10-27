package com.matthew.recipe_backend.services;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.RecipeImportRequest;
import com.matthew.recipe_backend.dtos.RecipeImportResponse;
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

    public String fetchPage(String url) throws IOException {
        Document document = Jsoup.connect(url)
                .userAgent("Mozilla/5.0")
                .timeout(10000)
                .get();

        return document.html();
    }
    // public RecipeImportResponse importRecipe(String url) {

    // }
}
