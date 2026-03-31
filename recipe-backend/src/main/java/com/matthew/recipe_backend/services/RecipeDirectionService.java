package com.matthew.recipe_backend.services;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.CreateDirectionDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.UpdateDirectionDto;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeDirection;
import com.matthew.recipe_backend.repositories.RecipeDirectionRepository;
import com.matthew.recipe_backend.repositories.RecipeRepository;
import com.matthew.recipe_backend.validators.RecipeDirectionValidator;
import com.matthew.recipe_backend.validators.RecipeValidator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class RecipeDirectionService {

    final private RecipeDirectionRepository recipeDirectionRepository;
    private final RecipeRepository recipeRepository;

    public RecipeDirectionService(RecipeDirectionRepository recipeDirectionRepository,
            RecipeRepository recipeRepository) {
        this.recipeDirectionRepository = recipeDirectionRepository;
        this.recipeRepository = recipeRepository;
    }

    public RecipeDto addRecipeDirection(Long recipeId, CreateDirectionDto newDirection) {
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        recipeRepository.findByIdWithIngredients(recipeId);
        RecipeValidator.validateDraftStatus(recipe);
        RecipeValidator.recipeBelongsToUser(recipe);

        RecipeDirection recipeDirection = new RecipeDirection();

        recipeDirection.setRecipe(recipe);
        recipeDirection.setDescription(newDirection.description());
        recipeDirection.setStepNumber(newDirection.stepNumber());

        RecipeDirectionValidator.validateRecipeDirection(recipeDirection);
        recipeDirectionRepository.save(recipeDirection);

        recipe.getRecipeDirection().add(recipeDirection);

        return RecipeMapper.toDto(recipe);
    }

    public RecipeDto editRecipeDirection(Long recipeId, Long directionId, UpdateDirectionDto updatedDirection) {
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        recipeRepository.findByIdWithIngredients(recipeId);
        RecipeValidator.validateDraftStatus(recipe);
        RecipeValidator.recipeBelongsToUser(recipe);

        RecipeDirection recipeDirection = recipeDirectionRepository.findById(directionId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe direction not found with the provided id"));
        if (!recipeDirection.getRecipe().getId().equals(recipeId)) {
            throw new IllegalStateException("Direction does not belong to the recipe");
        }

        recipeDirection.setDescription(updatedDirection.description());

        RecipeDirectionValidator.validateRecipeDirection(recipeDirection);
        recipeDirectionRepository.save(recipeDirection);

        return RecipeMapper.toDto(recipe);
    }

    public void deleteRecipeDirection(Long recipeId, Long directionId) {
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        recipeRepository.findByIdWithIngredients(recipeId);
        RecipeValidator.validateDraftStatus(recipe);
        RecipeValidator.recipeBelongsToUser(recipe);

        RecipeDirection recipeDirection = recipeDirectionRepository.findById(directionId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe direction not found with the provided id"));
        if (!recipeDirection.getRecipe().getId().equals(recipeId)) {
            throw new IllegalStateException("Direction does not belong to the recipe");
        }

    }
}
