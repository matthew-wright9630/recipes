package com.matthew.recipe_backend.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.CreateRecipeDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.StatusUpdateRequestDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeDto;
import com.matthew.recipe_backend.dtos.UserDto;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.services.RecipeService;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

	@GetMapping("/me")
	public ResponseEntity<List<RecipeDto>> getRecipeByUser(@AuthenticationPrincipal User user) {
		List<RecipeDto> recipes = recipeService.findRecipeByCreatedBy(user.getUsername());
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("/history")
	public ResponseEntity<List<RecipeDto>> getRecipeHistory(@AuthenticationPrincipal User user,
			@RequestParam(defaultValue = "3") int limit) {
		List<RecipeDto> recipes = recipeService.findRecentlyViewedRecipes(user, limit);
		return ResponseEntity.ok(recipes);
	}

	@PostMapping
	public ResponseEntity<RecipeDto> createDraftRecipe(
			@RequestBody CreateRecipeDto newRecipe,
			@AuthenticationPrincipal User user) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(recipeService.createDraftRecipe(newRecipe, user));
	}

	@PutMapping("/{id}/draft")
	public ResponseEntity<RecipeDto> editRecipe(@PathVariable long id, @RequestBody UpdateRecipeDto request) {
		RecipeDto recipe = recipeService.updateRecipe(id, request);
		return ResponseEntity.ok(recipe);
	}

	@PutMapping("/{id}/publish")
	public ResponseEntity<RecipeDto> publishRecipe(@PathVariable long id, @RequestBody UpdateRecipeDto request) {
		RecipeDto recipe = recipeService.saveAndPublishRecipe(id, request);
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
