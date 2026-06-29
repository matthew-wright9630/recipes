package com.matthew.recipe_backend.services;

import java.util.List;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.CreateRecipeDto;
import com.matthew.recipe_backend.dtos.RecipeDirectionsDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.dtos.RecipeIngredientDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeDirectionsDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeDto;
import com.matthew.recipe_backend.dtos.UpdateRecipeIngredientsDto;
import com.matthew.recipe_backend.dtos.UserDto;
import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.exceptions.UserNotFoundException;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Ingredient;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeDirection;
import com.matthew.recipe_backend.models.RecipeIngredient;
import com.matthew.recipe_backend.models.RecipeView;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.IngredientRepository;
import com.matthew.recipe_backend.repositories.RecipeDirectionRepository;
import com.matthew.recipe_backend.repositories.RecipeRepository;
import com.matthew.recipe_backend.repositories.RecipeViewRepository;
import com.matthew.recipe_backend.repositories.UserRepository;
import com.matthew.recipe_backend.validators.RecipeValidator;

import jakarta.persistence.EntityManager;
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
	private final UserRepository userRepository;
	private final RecipeIngredientService recipeIngredientService;
	private final RecipeViewService recipeViewService;
	private final RecipeViewRepository recipeViewRepository;
	private final RecipeDirectionRepository recipeDirectionRepository;
	private final IngredientRepository ingredientRepository;
	private final EntityManager entityManager;

	/**
	 * Constructs a {@code RecipeService} with the required repository dependency.
	 *
	 * @param recipeRepository the repository used for recipe persistence operations
	 */
	public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository,
			RecipeIngredientService recipeIngredientService, RecipeViewService recipeViewService,
			RecipeViewRepository recipeViewRepository, RecipeDirectionRepository recipeDirectionRepository,
			IngredientRepository ingredientRepository, EntityManager entityManager) {
		this.recipeRepository = recipeRepository;
		this.userRepository = userRepository;
		this.recipeIngredientService = recipeIngredientService;
		this.recipeViewService = recipeViewService;
		this.recipeViewRepository = recipeViewRepository;
		this.recipeDirectionRepository = recipeDirectionRepository;
		this.ingredientRepository = ingredientRepository;
		this.entityManager = entityManager;
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
		recipeViewService.addView(recipe);
		return recipeDto;
	}

	public List<RecipeDto> findRecipeByCreatedBy(String username) {
		User user = userRepository.findByEmail(username)
				.orElseThrow(() -> new UserNotFoundException(username));

		return recipeRepository.findByCreatedBy(user)
				.stream()
				.map(RecipeMapper::toDto)
				.toList();
	}

	public List<RecipeDto> findRecentlyViewedRecipes(User user, int limit) {
		return recipeViewRepository
				.findRecentViewsByUser(user, PageRequest.of(0, limit))
				.stream()
				.map(RecipeView::getRecipe)
				.map(RecipeMapper::toDto)
				.toList();
	}

	/**
	 * Creates a new recipe in {@code DRAFT} status with only a name and owner set.
	 *
	 * @param createdById the ID of the user creating the recipe
	 * @param draftRecipe the initial name for the recipe
	 * @return the newly created {@link RecipeDto}
	 */
	public RecipeDto createDraftRecipe(CreateRecipeDto draftRecipe, User user) {
		Recipe recipe = new Recipe(user, draftRecipe.name(), draftRecipe.description());
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
	public RecipeDto updateRecipe(Long id, UpdateRecipeDto recipeDto, User user) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		RecipeValidator.recipeBelongsToUser(foundRecipe, id);

		// Only draft recipes may be edited
		RecipeValidator.validateDraftStatus(foundRecipe);

		foundRecipe.setName(recipeDto.name());
		foundRecipe.setDescription(recipeDto.description());
		foundRecipe.setNotes(recipeDto.notes());
		foundRecipe.setServings(recipeDto.servings());
		foundRecipe.setPrepTime(recipeDto.prepTime());
		foundRecipe.setCookTime(recipeDto.cookTime());

		// Deletes old steps and saves the new ones
		updateDirections(foundRecipe, recipeDto.recipeDirections());

		// Delete old ingredients and save the new ones
		updateIngredients(foundRecipe, recipeDto.recipeIngredients());

		// Sort the ingredient list.
		recipeIngredientService.computeAndSaveSortOrder(foundRecipe);

		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe);
	}

	@Transactional
	public void updateDirections(
			Recipe recipe,
			List<UpdateRecipeDirectionsDto> directionDtos) {

		recipe.getRecipeDirections().clear();

		for (int i = 0; i < directionDtos.size(); i++) {
			UpdateRecipeDirectionsDto dto = directionDtos.get(i);
			RecipeDirection direction = new RecipeDirection();
			direction.setDescription(dto.description());
			direction.setStepNumber(i + 1);
			direction.setRecipe(recipe);
			recipe.getRecipeDirections().add(direction);
		}
	}

	@Transactional
	public void updateIngredients(
			Recipe recipe,
			List<UpdateRecipeIngredientsDto> ingredientDtos) {

		recipe.getRecipeIngredients().clear();
		entityManager.flush();

		for (UpdateRecipeIngredientsDto dto : ingredientDtos) {

			String cleanName = dto.name().trim().replaceAll("\\s+", " ");
			String normalized = cleanName.toLowerCase();

			Ingredient ingredient = findOrCreateIngredient(cleanName, normalized);

			RecipeIngredient recipeIngredient = new RecipeIngredient();
			recipeIngredient.setRecipe(recipe);
			recipeIngredient.setIngredient(ingredient);
			recipeIngredient.setQuantity(dto.quantity());
			recipeIngredient.setUnit(dto.unit());
			recipeIngredient.setNotes(dto.notes());

			recipeIngredientService.computeAndSaveSortOrder(recipe);

			recipe.getRecipeIngredients().add(recipeIngredient);
		}
	}

	public Ingredient findOrCreateIngredient(String cleanName, String normalized) {
		return ingredientRepository
				.findByNormalizedName(normalized)
				.orElseGet(() -> {
					try {
						Ingredient i = new Ingredient();
						i.setName(cleanName);
						i.setNormalizedName(normalized);
						return ingredientRepository.save(i);
					} catch (DataIntegrityViolationException e) {
						// Another thread beat us to it, just fetch the one that was saved
						return ingredientRepository
								.findByNormalizedName(normalized)
								.orElseThrow();
					}
				});
	}

	public RecipeDto saveAndPublishRecipe(Long id, UpdateRecipeDto recipeDto, User user) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		RecipeValidator.recipeBelongsToUser(foundRecipe, id);

		// Only draft recipes may be edited
		RecipeValidator.validateDraftStatus(foundRecipe);

		foundRecipe.setName(recipeDto.name());
		foundRecipe.setDescription(recipeDto.description());
		foundRecipe.setNotes(recipeDto.notes());
		foundRecipe.setServings(recipeDto.servings());
		foundRecipe.setPrepTime(recipeDto.prepTime());
		foundRecipe.setCookTime(recipeDto.cookTime());
		foundRecipe.setVersion(foundRecipe.getVersion() + 1);

		// Deletes old steps and saves the new ones
		updateDirections(foundRecipe, recipeDto.recipeDirections());

		// Delete old ingredients and save the new ones
		updateIngredients(foundRecipe, recipeDto.recipeIngredients());

		RecipeValidator.validateRecipePublish(foundRecipe);
		foundRecipe.setStatus(RecipeStatus.PUBLISHED);
		// Sort the ingredient list.
		recipeIngredientService.computeAndSaveSortOrder(foundRecipe);

		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe);
	}

	public RecipeDto archiveRecipe(Long id, User user) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		RecipeValidator.recipeBelongsToUser(foundRecipe, id);

		RecipeValidator.validateStatusTransition(foundRecipe.getStatus(), RecipeStatus.ARCHIVED);
		foundRecipe.setStatus(RecipeStatus.ARCHIVED);
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

	/**
	 * Creates a new revision of a recipe. Recipe needs to be in either PUBLISHED or
	 * ARCHIVED state.
	 * 
	 * 
	 */
	public RecipeDto createRevision(Long id, User user) {
		Recipe foundRecipe = recipeRepository.findById(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		RecipeValidator.validateCanCreateRevision(foundRecipe.getStatus());
		RecipeValidator.recipeBelongsToUser(foundRecipe, id);

		Recipe rootRecipe = foundRecipe.getRootRecipe() != null
				? foundRecipe.getRootRecipe()
				: foundRecipe;

		Recipe newDraftRecipe = new Recipe(foundRecipe.getName(), foundRecipe.getDescription(), foundRecipe.getNotes(),
				foundRecipe.getServings(), foundRecipe.getPrepTime(), foundRecipe.getCookTime(),
				foundRecipe.getCreatedBy(), foundRecipe.getVersion() + 1, rootRecipe);

		List<RecipeDirection> copiedDirections = foundRecipe.getRecipeDirections().stream()
				.map(d -> new RecipeDirection(newDraftRecipe, d))
				.toList();

		List<RecipeIngredient> copiedIngredients = foundRecipe.getRecipeIngredients().stream()
				.map(i -> new RecipeIngredient(newDraftRecipe, i))
				.toList();

		newDraftRecipe.setRecipeDirections(copiedDirections);
		newDraftRecipe.setRecipeIngredients(copiedIngredients);

		recipeRepository.save(newDraftRecipe);

		return RecipeMapper.toDto(newDraftRecipe);
	}
}