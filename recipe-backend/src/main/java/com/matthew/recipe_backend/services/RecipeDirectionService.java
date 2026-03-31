package com.matthew.recipe_backend.services;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.CreateDirectionDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.ReorderDirectionDto;
import com.matthew.recipe_backend.dtos.UpdateDirectionDto;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeDirection;
import com.matthew.recipe_backend.models.User;
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
    private final UserService userService;

    public RecipeDirectionService(RecipeDirectionRepository recipeDirectionRepository,
            RecipeRepository recipeRepository, UserService userService) {
        this.recipeDirectionRepository = recipeDirectionRepository;
        this.recipeRepository = recipeRepository;
        this.userService = userService;
    }

    public RecipeDto addRecipeDirection(Long recipeId, CreateDirectionDto newDirection) {
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        RecipeValidator.validateDraftStatus(recipe);

        Long userId = findUserId();
        RecipeValidator.recipeBelongsToUser(recipe, userId);

        RecipeDirection recipeDirection = new RecipeDirection();

        recipeDirection.setRecipe(recipe);
        recipeDirection.setDescription(newDirection.description());
        recipeDirection.setStepNumber(newDirection.stepNumber());

        RecipeDirectionValidator.validateRecipeDirection(recipeDirection);
        recipeDirectionRepository.save(recipeDirection);

        recipe.getRecipeDirections().add(recipeDirection);

        return RecipeMapper.toDto(recipe);
    }

    public RecipeDto editRecipeDirection(Long recipeId, Long directionId, UpdateDirectionDto updatedDirection) {
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        RecipeValidator.validateDraftStatus(recipe);

        Long userId = findUserId();
        RecipeValidator.recipeBelongsToUser(recipe, userId);

        RecipeDirection recipeDirection = recipeDirectionRepository.findById(directionId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe direction not found with the provided id"));

        recipeDirection.setDescription(updatedDirection.description());

        RecipeDirectionValidator.validateRecipeDirection(recipeDirection);
        recipeDirectionRepository.save(recipeDirection);

        return RecipeMapper.toDto(recipe);
    }

    public void deleteRecipeDirection(Long recipeId, Long directionId) {
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
        RecipeValidator.validateDraftStatus(recipe);

        Long userId = findUserId();
        RecipeValidator.recipeBelongsToUser(recipe, userId);

        recipe.getRecipeDirections().removeIf(rd -> rd.getId().equals(directionId));
        recipeRepository.save(recipe);
    }

    public RecipeDto reorderDirections(Long recipeId, List<ReorderDirectionDto> request) {
        RecipeDirectionValidator.validateReorder(request);
        Recipe recipe = recipeRepository.findByIdWithDirections(recipeId)
                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

        Map<Long, Integer> reorderMap = request.stream()
                .collect(Collectors.toMap(ReorderDirectionDto::directionId, ReorderDirectionDto::stepNumber));

        recipe.getRecipeDirections().forEach(rd -> {
            if (reorderMap.containsKey(rd.getId())) {
                rd.setStepNumber(reorderMap.get(rd.getId()));
            }
        });

        Recipe saved = recipeRepository.save(recipe);
        return RecipeMapper.toDto(saved);
    }

    public Long findUserId() {
        String username = SecurityContextHolder.getContext()
                .getAuthentication()
                .getName();

        User user = userService.findByUsername(username);

        return user.getId();
    }
}
