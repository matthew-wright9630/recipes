package com.matthew.recipe_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.CreateRecipeDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.StatusUpdateRequestDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeDto;
import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.services.RecipeService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;

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

	@PostMapping
	public ResponseEntity<RecipeDto> postRecipe(@RequestBody CreateRecipeDto request) {
		RecipeDto recipe = recipeService.createDraftRecipe(request.createdById(), request.name());
		return ResponseEntity.ok(recipe);
	}

	@PutMapping("/{id}")
	public ResponseEntity<RecipeDto> editRecipe(@PathVariable long id, @RequestBody UpdateRecipeDto request) {
		RecipeDto recipe = recipeService.updateRecipe(id, request);
		return ResponseEntity.ok(recipe);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteRecipe(@PathVariable long id) {
		recipeService.deleteDraftRecipe(id);
		return ResponseEntity.noContent().build();
	}

	@PatchMapping("{id}/status")
	public ResponseEntity<RecipeDto> updateRecipeStatus(@PathVariable long id,
			@RequestBody StatusUpdateRequestDto request) {
		RecipeDto recipe = recipeService.updateRecipeStatus(id, request.getStatus());
		return ResponseEntity.ok(recipe);
	}

}
