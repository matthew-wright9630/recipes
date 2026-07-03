package com.matthew.recipe_backend.services;

import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
import com.matthew.recipe_backend.models.RecipeLike;
import com.matthew.recipe_backend.models.RecipeView;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.IngredientRepository;
import com.matthew.recipe_backend.repositories.RecipeDirectionRepository;
import com.matthew.recipe_backend.repositories.RecipeLikeRepository;
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
	private final RecipeLikeRepository recipeLikeRepository;
	private final EntityManager entityManager;

	/**
	 * Constructs a {@code RecipeService} with the required repository dependency.
	 *
	 * @param recipeRepository the repository used for recipe persistence operations
	 */
	public RecipeService(RecipeRepository recipeRepository, UserRepository userRepository,
			RecipeIngredientService recipeIngredientService, RecipeViewService recipeViewService,
			RecipeViewRepository recipeViewRepository, RecipeDirectionRepository recipeDirectionRepository,
			IngredientRepository ingredientRepository, RecipeLikeRepository recipeLikeRepository,
			EntityManager entityManager) {
		this.recipeRepository = recipeRepository;
		this.userRepository = userRepository;
		this.recipeIngredientService = recipeIngredientService;
		this.recipeViewService = recipeViewService;
		this.recipeViewRepository = recipeViewRepository;
		this.recipeDirectionRepository = recipeDirectionRepository;
		this.ingredientRepository = ingredientRepository;
		this.recipeLikeRepository = recipeLikeRepository;
		this.entityManager = entityManager;
	}

	/**
	 * Retrieves all recipes along with their associated ingredients.
	 *
	 * @return a list of {@link RecipeDto} representing all recipes in the system
	 */
	public List<RecipeDto> findAllRecipes(User user) {
		List<Recipe> recipes = recipeRepository.findAllWithIngredients();

		List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		Set<Long> likedIds = getLikedRecipeIds(recipeIds, user.getId());

		return recipes.stream().map(recipe -> RecipeMapper.toDto(
				recipe,
				likeCountMap.getOrDefault(recipe.getId(), 0),
				likedIds.contains(recipe.getId()))).toList();
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
	public RecipeDto findRecipeById(Long id, User user) {
		// Fetch the recipe with its directions first, throwing if not found
		Recipe recipe = recipeRepository.findByIdWithDirections(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		// Separately fetch ingredients to populate the Hibernate session cache,
		// avoiding a multi-bag fetch exception on the same entity
		recipeRepository.findByIdWithIngredients(id);

		List<Long> recipeIds = List.of(recipe.getId());
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		Set<Long> likedIds = getLikedRecipeIds(recipeIds, user.getId());

		RecipeDto recipeDto = RecipeMapper.toDto(recipe,
				likeCountMap.getOrDefault(recipe.getId(), 0),
				likedIds.contains(recipe.getId()));
		recipeViewService.addView(recipe);
		return recipeDto;
	}

	public List<RecipeDto> findRecipeByCreatedBy(User user) {

		List<Recipe> recipes = recipeRepository.findByCreatedBy(user);

		List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		Set<Long> likedIds = getLikedRecipeIds(recipeIds, user.getId());

		return recipes.stream().map(recipe -> RecipeMapper.toDto(
				recipe,
				likeCountMap.getOrDefault(recipe.getId(), 0),
				likedIds.contains(recipe.getId()))).toList();
	}

	public List<RecipeDto> findRecentlyViewedRecipes(User user, int limit) {
		List<Recipe> recipes = recipeViewRepository
				.findDistinctRecentlyViewedRecipes(user.getId(), limit);

		List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		Set<Long> likedIds = getLikedRecipeIds(recipeIds, user.getId());

		return recipes.stream().map(recipe -> RecipeMapper.toDto(
				recipe,
				likeCountMap.getOrDefault(recipe.getId(), 0),
				likedIds.contains(recipe.getId()))).toList();
	}

	public Page<RecipeDto> findAllPublishedRecipes(Pageable pageable, String search, User currentUser) {
		Long userId = currentUser != null ? currentUser.getId() : null;
		Page<Recipe> recipes = search.isBlank()
				? recipeRepository.findAllByStatus(RecipeStatus.PUBLISHED, pageable)
				: recipeRepository.findAllByStatusAndNameContainingIgnoreCase(RecipeStatus.PUBLISHED, search, pageable);

		List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		Set<Long> likedIds = getLikedRecipeIds(recipeIds, userId);

		return recipes.map(recipe -> RecipeMapper.toDto(
				recipe,
				likeCountMap.getOrDefault(recipe.getId(), 0),
				likedIds.contains(recipe.getId())));
	}

	public List<RecipeDto> findLikedRecipePreview(User user) {
		Pageable pageable = PageRequest.of(0, 3);

		Page<Recipe> recipes = recipeRepository.findLikedRecipesByUserId(user.getId(), pageable);
		List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		return recipes.stream()
				.map(recipe -> RecipeMapper.toDto(recipe,
						likeCountMap.getOrDefault(recipe.getId(), 0),
						true))
				.toList();
	}

	public Page<RecipeDto> findAllLikedRecipesByUser(Pageable pageable, User user) {
		Page<Recipe> recipes = recipeRepository.findLikedRecipesByUserId(user.getId(), pageable);
		List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
		Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
		return recipes
				.map(recipe -> RecipeMapper.toDto(recipe,
						likeCountMap.getOrDefault(recipe.getId(), 0),
						true));
	}

	/**
	 * Creates a new recipe in {@code DRAFT} status with only a name and owner set.
	 *
	 * @param createdById the ID of the user creating the recipe
	 * @param draftRecipe the initial name for the recipe
	 * @return the newly created {@link RecipeDto}
	 */
	public RecipeDto createDraftRecipe(CreateRecipeDto draftRecipe, User user) {
		Recipe recipe = new Recipe(user, draftRecipe.name(), draftRecipe.description(), draftRecipe.imageUrl());
		recipeRepository.save(recipe);
		return RecipeMapper.toDto(recipe, 0, false);
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

		RecipeValidator.recipeBelongsToUser(foundRecipe, user.getId());

		// Only draft recipes may be edited
		RecipeValidator.validateDraftStatus(foundRecipe);

		foundRecipe.setName(recipeDto.name());
		foundRecipe.setDescription(recipeDto.description());
		foundRecipe.setNotes(recipeDto.notes());
		foundRecipe.setServings(recipeDto.servings());
		foundRecipe.setPrepTime(recipeDto.prepTime());
		foundRecipe.setCookTime(recipeDto.cookTime());
		foundRecipe.setUpdatedAt(OffsetDateTime.now());

		// Deletes old steps and saves the new ones
		updateDirections(foundRecipe, recipeDto.recipeDirections());

		// Delete old ingredients and save the new ones
		updateIngredients(foundRecipe, recipeDto.recipeIngredients());

		// Sort the ingredient list.
		recipeIngredientService.computeAndSaveSortOrder(foundRecipe);

		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe, 0, false);
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

		RecipeValidator.recipeBelongsToUser(foundRecipe, user.getId());

		// Only draft recipes may be edited
		RecipeValidator.validateDraftStatus(foundRecipe);

		foundRecipe.setName(recipeDto.name());
		foundRecipe.setDescription(recipeDto.description());
		foundRecipe.setNotes(recipeDto.notes());
		foundRecipe.setServings(recipeDto.servings());
		foundRecipe.setPrepTime(recipeDto.prepTime());
		foundRecipe.setCookTime(recipeDto.cookTime());
		foundRecipe.setUpdatedAt(OffsetDateTime.now());
		foundRecipe.setVersion(foundRecipe.getVersion() + 1);

		// Deletes old steps and saves the new ones
		updateDirections(foundRecipe, recipeDto.recipeDirections());

		// Delete old ingredients and save the new ones
		updateIngredients(foundRecipe, recipeDto.recipeIngredients());

		// Find the previous version and SUPERSEDE it with the new revision.
		if (foundRecipe.getRootRecipe() != null) {
			Long rootId = foundRecipe.getRootRecipe().getId();

			// Check if the root recipe (v1) is the active one
			recipeRepository.findByIdAndStatusIn(rootId, List.of(RecipeStatus.PUBLISHED, RecipeStatus.ARCHIVED))
					.ifPresent(previous -> {
						previous.setStatus(RecipeStatus.SUPERSEDED);
						recipeRepository.save(previous);
					});

			// Check if any other revision (v2+) is the active one
			recipeRepository
					.findByRootRecipeIdAndStatusIn(rootId, List.of(RecipeStatus.PUBLISHED, RecipeStatus.ARCHIVED))
					.ifPresent(previous -> {
						previous.setStatus(RecipeStatus.SUPERSEDED);
						recipeRepository.save(previous);
					});
		}

		RecipeValidator.validateRecipePublish(foundRecipe);
		foundRecipe.setStatus(RecipeStatus.PUBLISHED);
		// Sort the ingredient list.
		recipeIngredientService.computeAndSaveSortOrder(foundRecipe);

		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe, 0, false);
	}

	public RecipeDto archiveRecipe(Long id, User user) {
		Recipe foundRecipe = recipeRepository.findByIdWithIngredients(id)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found with the provided id"));

		RecipeValidator.recipeBelongsToUser(foundRecipe, user.getId());

		RecipeValidator.validateStatusTransition(foundRecipe.getStatus(), RecipeStatus.ARCHIVED);
		foundRecipe.setUpdatedAt(OffsetDateTime.now());
		foundRecipe.setStatus(RecipeStatus.ARCHIVED);
		Recipe savedRecipe = recipeRepository.save(foundRecipe);
		return RecipeMapper.toDto(savedRecipe, 0, false);
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
		RecipeValidator.recipeBelongsToUser(foundRecipe, user.getId());

		Recipe rootRecipe = foundRecipe.getRootRecipe() != null
				? foundRecipe.getRootRecipe()
				: foundRecipe;

		Recipe newDraftRecipe = new Recipe(foundRecipe.getName(), foundRecipe.getDescription(),
				foundRecipe.getImageUrl(), foundRecipe.getNotes(),
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

		return RecipeMapper.toDto(newDraftRecipe, 0, false);
	}

	public void likeRecipe(Long recipeId, User user) {
		Recipe recipe = recipeRepository.findById(recipeId)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

		if (recipeLikeRepository.existsByRecipeIdAndUserId(recipeId, user.getId())) {
			throw new IllegalStateException("Recipe already liked");
		}
		if (recipe.getStatus() != RecipeStatus.PUBLISHED) {
			throw new IllegalStateException("Only published recipes can be liked");
		}

		RecipeLike like = new RecipeLike(user, recipe, OffsetDateTime.now());
		recipeLikeRepository.save(like);
	}

	public void unlikeRecipe(Long recipeId, User user) {
		Recipe recipe = recipeRepository.findById(recipeId)
				.orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

		if (!recipeLikeRepository.existsByRecipeIdAndUserId(recipeId, user.getId())) {
			throw new IllegalStateException("Recipe not liked");
		}

		if (recipe.getStatus() != RecipeStatus.PUBLISHED) {
			throw new IllegalStateException("Only published recipes can be liked");
		}

		recipeLikeRepository.deleteByRecipeIdAndUserId(recipeId, user.getId());
	}

	private Map<Long, Integer> getLikeCountMap(List<Long> recipeIds) {
		return recipeLikeRepository.countLikesByRecipeIds(recipeIds)
				.stream()
				.collect(Collectors.toMap(
						row -> (Long) row[0],
						row -> ((Long) row[1]).intValue()));
	}

	private Set<Long> getLikedRecipeIds(List<Long> recipeIds, Long userId) {
		if (userId == null)
			return Set.of();
		return new HashSet<>(recipeLikeRepository.findLikedRecipeIds(recipeIds, userId));
	}
}