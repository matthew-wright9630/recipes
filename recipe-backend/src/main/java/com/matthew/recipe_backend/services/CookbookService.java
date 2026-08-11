package com.matthew.recipe_backend.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.AddRecipeDto;
import com.matthew.recipe_backend.dtos.CookbookDetailsDto;
import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.dtos.CookbookRecipeSelectionDto;
import com.matthew.recipe_backend.dtos.CookbookWithRecipesDto;
import com.matthew.recipe_backend.dtos.CreateCookbookDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.exceptions.CookbookExistsException;
import com.matthew.recipe_backend.exceptions.UserNotFoundException;
import com.matthew.recipe_backend.mappers.CookbookMapper;
import com.matthew.recipe_backend.mappers.CookbookRecipeSelectionMapper;
import com.matthew.recipe_backend.mappers.CookbookWithRecipesMapper;
import com.matthew.recipe_backend.mappers.RecipeMapper;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.CookbookAccess;
import com.matthew.recipe_backend.models.CookbookRecipe;
import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.CookbookAccessRepository;
import com.matthew.recipe_backend.repositories.CookbookRecipeRepository;
import com.matthew.recipe_backend.repositories.CookbookRepository;
import com.matthew.recipe_backend.repositories.RecipeLikeRepository;
import com.matthew.recipe_backend.repositories.RecipeRepository;
import com.matthew.recipe_backend.repositories.RecipeViewRepository;
import com.matthew.recipe_backend.repositories.UserRepository;
import com.matthew.recipe_backend.validators.CookbookValidator;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class CookbookService {

        private final CookbookRepository cookbookRepository;
        private final CookbookAccessRepository cookbookAccessRepository;
        private final UserRepository userRepository;
        private final CookbookRecipeRepository cookbookRecipeRepository;
        private final RecipeRepository recipeRepository;
        private final RecipeLikeRepository recipeLikeRepository;
        private final RecipeViewRepository recipeViewRepository;

        public CookbookService(CookbookRepository cookbookRepository, UserRepository userRepository,
                        CookbookAccessRepository cookbookAccessRepository,
                        CookbookRecipeRepository cookbookRecipeRepository,
                        RecipeRepository recipeRepository, RecipeLikeRepository recipeLikeRepository,
                        RecipeViewRepository recipeViewRepository) {
                this.cookbookRepository = cookbookRepository;
                this.userRepository = userRepository;
                this.cookbookAccessRepository = cookbookAccessRepository;
                this.cookbookRecipeRepository = cookbookRecipeRepository;
                this.recipeRepository = recipeRepository;
                this.recipeLikeRepository = recipeLikeRepository;
                this.recipeViewRepository = recipeViewRepository;
        }

        // public List<CookbookDto> findMyCookbooks(String username) {
        // User user = userRepository.findByEmail(username.toLowerCase())
        // .orElseThrow(() -> new UserNotFoundException(username));

        // return cookbookAccessRepository
        // .findCookbooksByUserAndPermissions(
        // user,
        // List.of(
        // CookbookPermission.OWNER))
        // .stream()
        // .map(CookbookMapper::toDto)
        // .toList();
        // }

        // public List<CookbookDto> findSharedCookbooks(String username) {
        // User user = userRepository.findByEmail(username.toLowerCase())
        // .orElseThrow(() -> new UserNotFoundException(username));

        // return cookbookAccessRepository
        // .findCookbooksByUserAndPermissions(
        // user,
        // List.of(
        // CookbookPermission.READ,
        // CookbookPermission.READ_WRITE))
        // .stream()
        // .map(CookbookMapper::toDto)
        // .toList();
        // }

        public CookbookWithRecipesDto findCookbookById(User user, Long cookbookId) {
                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                CookbookValidator.assertUserHasAccessToCookbook(cookbookAccessRepository, cookbookId, user.getId());

                List<Recipe> recipes = cookbookRecipeRepository.findByCookbookIdAndStatus(
                                cookbookId,
                                RecipeStatus.PUBLISHED)
                                .stream().map(CookbookRecipe::getRecipe)
                                .toList();

                List<Long> recipeIds = recipes.stream().map(Recipe::getId).toList();
                Map<Long, Integer> likeCountMap = getLikeCountMap(recipeIds);
                Set<Long> likedIds = getLikedRecipeIds(recipeIds, user.getId());

                Map<Long, Integer> viewCountMap = getViewCountMap(recipeIds);

                List<RecipeDto> recipeDtos = recipes.stream().map(recipe -> RecipeMapper.toDto(
                                recipe,
                                likeCountMap.getOrDefault(recipe.getId(), 0),
                                viewCountMap.getOrDefault(recipe.getId(), 0),
                                likedIds.contains(recipe.getId()))).toList();

                return CookbookWithRecipesMapper.toDto(foundCookbook, recipeDtos, user.getId());
        }

        public Page<CookbookDto> findAllAccessibleCookbooks(Pageable pageable, String search, User user) {
                Page<CookbookDto> cookbooks = search.isBlank() ? cookbookAccessRepository
                                .findCookbooksByUserAndPermissions(
                                                user,
                                                List.of(
                                                                CookbookPermission.OWNER,
                                                                CookbookPermission.READ,
                                                                CookbookPermission.READ_WRITE),
                                                pageable)
                                .map(CookbookMapper::toDto)
                                : cookbookAccessRepository
                                                .findCookbooksByUserAndPermissionsAndNameContainingIgnoreCase(
                                                                user.getId(),
                                                                List.of(
                                                                                CookbookPermission.OWNER,
                                                                                CookbookPermission.READ,
                                                                                CookbookPermission.READ_WRITE),
                                                                pageable,
                                                                search)
                                                .map(CookbookMapper::toDto);
                return cookbooks;
        }

        public List<CookbookRecipeSelectionDto> findAllEditableCookbooks(User user, Long recipeId) {
                List<Cookbook> cookbooks = cookbookAccessRepository.findCookbooksByUserIdAndPermissionIn(
                                user.getId(),
                                List.of(CookbookPermission.OWNER, CookbookPermission.READ_WRITE));

                Set<Long> recipeCookbookIds = cookbookRecipeRepository.findCookbookIdsByRecipeId(recipeId);

                return cookbooks.stream()
                                .map(cookbook -> CookbookRecipeSelectionMapper.toDto(
                                                cookbook,
                                                recipeCookbookIds.contains(cookbook.getId())))
                                .toList();
        }

        @Transactional
        public CookbookDto createCookbook(String username, CreateCookbookDto cookbookCreationDto) {
                User user = userRepository.findByEmail(username.toLowerCase())
                                .orElseThrow(() -> new UserNotFoundException(username));

                if (cookbookRepository.existsByOwner_IdAndName(user.getId(), cookbookCreationDto.name())) {
                        throw new CookbookExistsException("A cookbook by that name already exists");
                }

                Cookbook cookbook = new Cookbook(cookbookCreationDto.name(), cookbookCreationDto.description(),
                                cookbookCreationDto.imageUrl(), user);
                cookbookRepository.save(cookbook);
                CookbookAccess access = new CookbookAccess(cookbook, user, CookbookPermission.OWNER, Instant.now());
                cookbookAccessRepository.save(access);

                return CookbookMapper.toDto(cookbook);
        }

        @Transactional
        public CookbookDto addRecipeToCookbook(String username, Long cookbookId, AddRecipeDto addRecipeDto) {
                User user = userRepository.findByEmail(username.toLowerCase())
                                .orElseThrow(() -> new UserNotFoundException(username));

                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                Recipe foundRecipe = recipeRepository.findById(addRecipeDto.recipeId())
                                .orElseThrow(() -> new EntityNotFoundException("Recipe not found"));

                CookbookValidator.assertUserOwnsCookbook(cookbookAccessRepository, cookbookId, user.getId());

                CookbookRecipe newCookbookRecipe = new CookbookRecipe(foundCookbook, foundRecipe,
                                Instant.now());
                cookbookRecipeRepository.save(newCookbookRecipe);

                return CookbookMapper.toDto(foundCookbook);
        }

        private Map<Long, Integer> getLikeCountMap(List<Long> recipeIds) {
                return recipeLikeRepository.countLikesByRecipeIds(recipeIds)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (Long) row[0],
                                                row -> ((Long) row[1]).intValue()));
        }

        private Map<Long, Integer> getViewCountMap(List<Long> recipeIds) {
                return recipeViewRepository.countViewsByRecipeIds(recipeIds)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (Long) row[0],
                                                row -> ((Long) row[1]).intValue()));
        }

        @Transactional
        public CookbookDto editCookbook(User user, Long cookbookId, CreateCookbookDto cookbookEditDto) {
                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                CookbookValidator.assertUserOwnsCookbook(cookbookAccessRepository, cookbookId, user.getId());

                if (cookbookRepository.existsByOwner_IdAndNameAndIdNot(user.getId(), cookbookEditDto.name(),
                                foundCookbook.getId())) {
                        throw new CookbookExistsException("A cookbook by that name already exists");
                }

                foundCookbook.setImageUrl(cookbookEditDto.imageUrl());
                foundCookbook.setDescription(cookbookEditDto.description());
                foundCookbook.setName(cookbookEditDto.name());

                return CookbookMapper.toDto(foundCookbook);
        }

        private Set<Long> getLikedRecipeIds(List<Long> recipeIds, Long userId) {
                if (userId == null)
                        return Set.of();
                return new HashSet<>(recipeLikeRepository.findLikedRecipeIds(recipeIds, userId));
        }
}
