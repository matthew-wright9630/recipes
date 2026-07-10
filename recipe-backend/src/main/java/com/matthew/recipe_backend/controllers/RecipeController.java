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

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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
	public ResponseEntity<List<RecipeDto>> getAllRecipes(
			@AuthenticationPrincipal User user) {
		List<RecipeDto> recipes = recipeService.findAllRecipes(user);
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("/{id}")
	public ResponseEntity<RecipeDto> getRecipeById(@PathVariable long id,
			@AuthenticationPrincipal User user) {
		RecipeDto recipe = recipeService.findRecipeById(id, user);
		return ResponseEntity.ok(recipe);
	}

	@GetMapping("/me")
	public ResponseEntity<List<RecipeDto>> getRecipeByUser(@AuthenticationPrincipal User user) {
		List<RecipeDto> recipes = recipeService.findRecipeByCreatedBy(user);
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("/publish")
	public ResponseEntity<Page<RecipeDto>> getPublishedRecipes(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size,
			@RequestParam(defaultValue = "") String search,
			@AuthenticationPrincipal User user) {
		Pageable pageable = PageRequest.of(page, size);
		Page<RecipeDto> recipes = recipeService.findAllPublishedRecipes(pageable, search, user);
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("/me/history/preview")
	public ResponseEntity<List<RecipeDto>> getRecipeHistoryPreview(@AuthenticationPrincipal User user) {
		List<RecipeDto> recipes = recipeService.findRecentlyViewedRecipesPreview(user);
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("/me/history")
	public ResponseEntity<Page<RecipeDto>> getRecipeHistory(@AuthenticationPrincipal User user,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<RecipeDto> recipes = recipeService.findRecentlyViewedRecipes(user, pageable);
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("/me/liked/preview")
	public ResponseEntity<List<RecipeDto>> getLikedRecipes(@AuthenticationPrincipal User user) {
		List<RecipeDto> recipes = recipeService.findLikedRecipePreview(user);
		return ResponseEntity.ok(recipes);
	}

	@GetMapping("me/liked")
	public ResponseEntity<Page<RecipeDto>> getLikedRecipes(
			@AuthenticationPrincipal User user,
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "12") int size) {
		Pageable pageable = PageRequest.of(page, size);
		Page<RecipeDto> recipes = recipeService.findAllLikedRecipesByUser(pageable, user);
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
	public ResponseEntity<RecipeDto> editRecipe(@PathVariable long id, @RequestBody UpdateRecipeDto request,
			@AuthenticationPrincipal User user) {
		RecipeDto recipe = recipeService.updateRecipe(id, request, user);
		return ResponseEntity.ok(recipe);
	}

	@PutMapping("/{id}/publish")
	public ResponseEntity<RecipeDto> publishRecipe(@PathVariable long id, @RequestBody UpdateRecipeDto request,
			@AuthenticationPrincipal User user) {
		RecipeDto recipe = recipeService.saveAndPublishRecipe(id, request, user);
		return ResponseEntity.ok(recipe);
	}

	@PutMapping("/{id}/archive")
	public ResponseEntity<RecipeDto> archiveRecipe(@PathVariable long id,
			@AuthenticationPrincipal User user) {
		RecipeDto recipe = recipeService.archiveRecipe(id, user);
		return ResponseEntity.ok(recipe);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Object> deleteRecipe(@PathVariable long id,
			@AuthenticationPrincipal User user) {
		recipeService.deleteDraftRecipe(id);
		return ResponseEntity.noContent().build();
	}

	@PostMapping("/{id}/revise")
	public ResponseEntity<RecipeDto> reviseRecipe(@PathVariable long id, @AuthenticationPrincipal User user) {
		RecipeDto recipe = recipeService.createRevision(id, user);
		return ResponseEntity.ok(recipe);
	}

}
