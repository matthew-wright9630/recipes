package com.matthew.recipe_backend.services;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.Utils.CustomUserDetails;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeDto;
import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.RecipeRepository;
import com.matthew.recipe_backend.services.UserService;
import com.matthew.recipe_backend.validators.RecipeValidator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * Service layer for managing {@link Recipe} entities.
 *
 * <p>
 * Handles the core business logic for creating, retrieving, updating,
 * and deleting recipes. All operations are executed within a transaction.
 */
@Service
@Transactional
public class RecipeService {

	private final RecipeRepository recipeRepository;
	private final UserService userService;

	/**
	 * Constructs a {@code RecipeService} with the required repository dependency.
	 *
	 * @param recipeRepository the repository used for recipe persistence operations
	 */
	public RecipeService(RecipeRepository recipeRepository, UserService userService) {
		this.recipeRepository = recipeRepository;
		this.userService = userService;
	}

	/**
	 * Retrieves all recipes along with their associated ingredients.
	 *
	 * @return a list of {@link RecipeDto} representing all recipes in the system
	 */
	public List<RecipeDto> findAllRecipes() {
		List<Recipe> recipes = recipeRepository.findAllWithIngredients();
		List<RecipeDto> recipeDtos = recipes.stream().map(RecipeMapper::toDto).toList();
		return recipeDtos;
	}

	/**
	 * Retrieves a single recipe by its ID, including its directions and
	 * ingredients.
	 *
	 * <p>
	 * Two separate queries are issued to avoid a Cartesian product when
	 * fetching multiple collection associations simultaneously.
	 *
	 * @param id the ID of the recipe to retrieve
	 * @return the matching {@link RecipeDto}
	 * @throws EntityNotFoundException if no recipe exists with the given ID
	 */
	public RecipeDto findRecipeById(Long id) {
		// Fetch the recipe with its directions first, throwing if not found
		Recipe recipe = recipeRepository.findByIdWithDirections(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		// Separately fetch ingredients to populate the Hibernate session cache,
		// avoiding a multi-bag fetch exception on the same entity
		recipeRepository.findByIdWithIngredients(id);

		RecipeDto recipeDto = RecipeMapper.toDto(recipe);
		return recipeDto;
	}

	/**
	 * Creates a new recipe in {@code DRAFT} status with only a name and owner set.
	 *
	 * @param createdById the ID of the user creating the recipe
	 * @param name        the initial name for the recipe
	 * @return the newly created {@link RecipeDto}
	 */
	public RecipeDto createDraftRecipe(String name) {
		CustomUserDetails user = (CustomUserDetails) SecurityContextHolder.getContext().getAuthentication()
				.getPrincipal();
		Long userId = user.getId();

		User createdBy = userService.findById(userId);

		Recipe recipe = new Recipe(createdBy, name);
		recipeRepository.save(recipe);
		return RecipeMapper.toDto(recipe);
	}

	/**
	 * Updates the editable fields of an existing recipe.
	 *
	 * <p>
	 * The recipe must be in {@code DRAFT} status to be modified.
	 *
	 * @param id        the ID of the recipe to update
	 * @param recipeDto a DTO containing the updated field values
	 * @return the updated {@link RecipeDto}
	 * @throws EntityNotFoundException if no recipe exists with the given ID
	 * @throws IllegalStateException   if the recipe is not in {@code DRAFT} status
	 */
	public RecipeDto updateRecipe(Long id, UpdateRecipeDto recipeDto) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		// Only draft recipes may be edited
		RecipeValidator.validateDraftStatus(foundRecipe);

		foundRecipe.setName(recipeDto.name());
		foundRecipe.setDescription(recipeDto.description());
		foundRecipe.setNotes(recipeDto.notes());
		foundRecipe.setServings(recipeDto.servings());
		foundRecipe.setPrepTime(recipeDto.prepTime());
		foundRecipe.setCookTime(recipeDto.cookTime());

		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe);
	}

	/**
	 * Transitions a recipe to a new {@link RecipeStatus}.
	 *
	 * <p>
	 * If the target status is {@code PUBLISHED}, additional validation is
	 * performed to ensure the recipe is complete, and the version number is
	 * incremented to reflect the new publication.
	 *
	 * @param id        the ID of the recipe whose status is being updated
	 * @param newStatus the desired target status
	 * @return the updated {@link RecipeDto}
	 * @throws EntityNotFoundException if no recipe exists with the given ID
	 * @throws IllegalStateException   if the status transition is not permitted
	 * @throws IllegalStateException   if the recipe does not meet publish
	 *                                 requirements
	 */
	public RecipeDto updateRecipeStatus(Long id, RecipeStatus newStatus) {
		Recipe foundRecipe = recipeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		RecipeStatus currentStatus = foundRecipe.getStatus();

		// Validate that the requested transition is allowed by the status machine
		RecipeValidator.validateStatusTransition(currentStatus, newStatus);

		if (newStatus.equals(RecipeStatus.PUBLISHED)) {
			// Ensure the recipe has all required fields before going live
			RecipeValidator.validateRecipePublish(foundRecipe);
			// Bump the version number on each new publication
			foundRecipe.setVersion(foundRecipe.getVersion() + 1);
		}

		foundRecipe.setStatus(newStatus);
		return RecipeMapper.toDto(foundRecipe);
	}

	/**
	 * Deletes a recipe, provided it is still in {@code DRAFT} status.
	 *
	 * <p>
	 * Published or archived recipes cannot be deleted through this method.
	 *
	 * @param id the ID of the recipe to delete
	 * @throws EntityNotFoundException if no recipe exists with the given ID
	 * @throws IllegalStateException   if the recipe is not in {@code DRAFT} status
	 */
	public void deleteDraftRecipe(Long id) {
		Recipe foundRecipe = recipeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		// Guard against accidental deletion of non-draft recipes
		RecipeValidator.validateDraftStatus(foundRecipe);

		recipeRepository.delete(foundRecipe);
	}
}