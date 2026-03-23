package com.matthew.recipe_backend.services;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.repositories.RecipeRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecipeService {
    
    private final RecipeRepository recipeRepository;

    public RecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    public List<RecipeDto> findAllRecipes() {
        List<Recipe> recipes = recipeRepository.findAllWithIngredients();
        List<RecipeDto> recipeDtos = recipes.stream().map(RecipeMapper::toDto).toList();
        return recipeDtos;
        
    }

    public RecipeDto findRecipeById(long id) {
        Recipe recipe = recipeRepository.findByIdWithIngredients(id).orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        RecipeDto recipeDto = RecipeMapper.toDto(recipe);
        return recipeDto;
    }

    public RecipeDto createDraftRecipe(long createdById, String name) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setCreatedBy(createdById);
        recipe.setCreatedAt(OffsetDateTime.now());
        recipe.setDeleted(false);
        recipe.setVersion(0);
        System.out.println(recipe);
        recipeRepository.save(recipe);
        return RecipeMapper.toDto(recipe);
    }
}
