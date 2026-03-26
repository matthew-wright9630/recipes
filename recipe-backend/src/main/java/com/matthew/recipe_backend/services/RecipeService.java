package com.matthew.recipe_backend.services;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeDto;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.repositories.RecipeRepository;
import com.matthew.recipe_backend.validators.RecipeValidator;

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

	public RecipeDto findRecipeById(Long id) {
		Recipe recipe = recipeRepository.findByIdWithDirections(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
		recipeRepository.findByIdWithIngredients(id);
		RecipeDto recipeDto = RecipeMapper.toDto(recipe);
		return recipeDto;
	}

	public RecipeDto createDraftRecipe(Long createdById, String name) {
		Recipe recipe = new Recipe(createdById, name);
		recipeRepository.save(recipe);
		return RecipeMapper.toDto(recipe);
	}

	public RecipeDto updateRecipe(Long id, UpdateRecipeDto recipeDto) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
		foundRecipe.setName(recipeDto.name());
		foundRecipe.setDescription(recipeDto.description());
		foundRecipe.setNotes(recipeDto.notes());
		foundRecipe.setServings(recipeDto.servings());
		foundRecipe.setPrepTime(recipeDto.prepTime());
		foundRecipe.setCookTime(recipeDto.cookTime());
		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe);
	}

	public RecipeDto deactivateRecipe(Long id) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
		foundRecipe.setActive(false);
		return RecipeMapper.toDto(foundRecipe);
	}

	public RecipeDto reActivateRecipe(Long id) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
		foundRecipe.setActive(true);
		return RecipeMapper.toDto(foundRecipe);
	}

	public RecipeDto publishRecipe(Long id) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));
		RecipeValidator.validateRecipePublish(foundRecipe);
		foundRecipe.setPublished(true);
		foundRecipe.setVersion(foundRecipe.getVersion() + 1);
		return RecipeMapper.toDto(foundRecipe);

	}

}
