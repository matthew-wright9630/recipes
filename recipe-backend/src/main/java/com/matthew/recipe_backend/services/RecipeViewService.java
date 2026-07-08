package com.matthew.recipe_backend.services;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeView;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.RecipeViewRepository;

@Service
public class RecipeViewService {

    private final RecipeViewRepository recipeViewRepository;
    private final AuthService authService;
    private final VisitorService visitorService;

    public RecipeViewService(RecipeViewRepository recipeViewRepository, AuthService authService,
            VisitorService visitorService) {
        this.recipeViewRepository = recipeViewRepository;
        this.authService = authService;
        this.visitorService = visitorService;
    }

    public void addView(User user, Recipe recipe) {
        Instant timeDelay = Instant.now().minus(60, ChronoUnit.MINUTES);

        boolean viewedRecently = recipeViewRepository.existsByUserAndRecipeAndViewedAtAfter(user, recipe, timeDelay);

        if (!viewedRecently) {
            RecipeView recipeView = new RecipeView();

            recipeView.setUser(user);
            recipeView.setRecipe(recipe);
            recipeView.setViewedAt(Instant.now());

            recipeViewRepository.save(recipeView);
        }
    }

    public void addView(Recipe recipe) {
        Instant timeDelay = Instant.now().minus(60, ChronoUnit.MINUTES);

        User user = authService.getOptionalCurrentUser();
        String visitorId = visitorService.getOrCreateVisitorId();

        boolean viewedRecently = false;

        if (user != null) {
            viewedRecently = recipeViewRepository
                    .existsByUserAndRecipeAndViewedAtAfter(user, recipe, timeDelay);
        } else {
            viewedRecently = recipeViewRepository
                    .existsByRecipeAndVisitorIdAndViewedAtAfter(recipe, visitorId, timeDelay);
        }

        if (!viewedRecently) {
            RecipeView recipeView = new RecipeView();

            if (user != null) {
                recipeView.setUser(user);
                recipeView.setVisitorId(null);
            } else {
                recipeView.setUser(null);
                recipeView.setVisitorId(visitorId);
            }
            recipeView.setRecipe(recipe);
            recipeView.setViewedAt(Instant.now());

            recipeViewRepository.save(recipeView);
        }
    }

}
