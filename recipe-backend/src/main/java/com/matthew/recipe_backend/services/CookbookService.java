package com.matthew.recipe_backend.services;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.matthew.recipe_backend.dtos.ShareCookbookDto;
import com.matthew.recipe_backend.dtos.SharedUserDto;
import com.matthew.recipe_backend.dtos.UpdateCookbookAccessDto;
import com.matthew.recipe_backend.dtos.UpdateUserPermission;
import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.enums.CookbookType;
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

                Map<Long, Integer> savedCountMap = getSavedCountMap(recipeIds);
                Set<Long> bookmarkedIds = getBookmarkedRecipeIds(recipeIds, user.getId());

                List<RecipeDto> recipeDtos = recipes.stream().map(recipe -> RecipeMapper.toDto(
                                recipe,
                                likeCountMap.getOrDefault(recipe.getId(), 0),
                                savedCountMap.getOrDefault(recipe.getId(), 0),
                                likedIds.contains(recipe.getId()),
                                bookmarkedIds.contains(recipe.getId()))).toList();

                CookbookPermission permission = cookbookAccessRepository.findPermissionByCookbookIdAndUserId(cookbookId,
                                user.getId());

                return CookbookWithRecipesMapper.toDto(foundCookbook, recipeDtos, user.getId(), permission);
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
                                                recipeCookbookIds.contains(cookbook.getId()),
                                                cookbook.getOwner().getId(),
                                                cookbook.getOwner().getDisplayUsername()))
                                .toList();
        }

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

        private Map<Long, Integer> getSavedCountMap(List<Long> recipeIds) {
                return cookbookRecipeRepository.countBookmarksByRecipeIds(recipeIds)
                                .stream()
                                .collect(Collectors.toMap(
                                                row -> (Long) row[0],
                                                row -> ((Long) row[1]).intValue()));
        }

        private Set<Long> getBookmarkedRecipeIds(List<Long> recipeIds, Long userId) {
                if (userId == null)
                        return Set.of();
                return new HashSet<>(
                                cookbookRecipeRepository.findRecipeIdsByUserIdAndCookbookType(userId,
                                                CookbookType.BOOKMARK));
        }

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

        public void removeCookbook(User user, Long cookbookId) {
                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                CookbookValidator.assertUserOwnsCookbook(cookbookAccessRepository, cookbookId, user.getId());

                foundCookbook.setDeleted(true);
                foundCookbook.setUpdatedAt(OffsetDateTime.now());
                cookbookRepository.save(foundCookbook);
        }

        public List<SharedUserDto> findAllSharedUsers(User user, Long cookbookId) {
                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                CookbookValidator.assertUserHasAccessToCookbook(cookbookAccessRepository, cookbookId, user.getId());

                List<SharedUserDto> sharedUsers = cookbookAccessRepository
                                .findAllByCookbookIdOrderByGrantedAtAsc(cookbookId).stream()
                                .map(cookbookAccess -> new SharedUserDto(cookbookAccess.getUser().getId(),
                                                cookbookAccess.getUser().getDisplayUsername(),
                                                cookbookAccess.getUser().getAvatarUrl(),
                                                cookbookAccess.getPermission()))
                                .toList();
                return sharedUsers;
        }

        public void shareCookbook(Long cookbookId, User user, ShareCookbookDto request) {
                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                CookbookValidator.assertUserHasEditAccess(cookbookAccessRepository, cookbookId, user.getId());

                for (Long userId : request.userIds()) {
                        User userToShare = userRepository.findById(userId)
                                        .orElseThrow(() -> new UserNotFoundException("User does not exist"));

                        if (cookbookAccessRepository.existsByCookbookIdAndUserId(cookbookId, userToShare.getId())) {
                                continue;
                        }

                        cookbookAccessRepository.save(new CookbookAccess(foundCookbook, userToShare,
                                        CookbookPermission.READ, Instant.now()));
                }

                for (String email : request.emails()) {
                        userRepository.findByEmail(email)
                                        .ifPresent(userToShare -> {
                                                if (cookbookAccessRepository.existsByCookbookIdAndUserId(
                                                                cookbookId, userToShare.getId())) {
                                                        return;
                                                }

                                                cookbookAccessRepository.save(new CookbookAccess(
                                                                foundCookbook,
                                                                userToShare,
                                                                CookbookPermission.READ,
                                                                Instant.now()));
                                        });
                }
        }

        public List<SharedUserDto> updateCookbookAccess(Long cookbookId, User user, UpdateCookbookAccessDto request) {
                Cookbook foundCookbook = cookbookRepository.findById(cookbookId)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));
                CookbookValidator.assertUserOwnsCookbook(cookbookAccessRepository, cookbookId, user.getId());

                for (UpdateUserPermission userUpdate : request.users()) {
                        CookbookAccess cookbookAccess = cookbookAccessRepository
                                        .findByCookbookIdAndUserId(cookbookId, userUpdate.userId())
                                        .orElseThrow(() -> new EntityNotFoundException(
                                                        "User does not have access to this cookbook"));

                        if (cookbookAccess.getPermission() == CookbookPermission.OWNER) {
                                throw new IllegalArgumentException("Cannot modify cookbook owner");
                        }

                        if (userUpdate.removed()) {
                                cookbookAccessRepository.delete(cookbookAccess);
                        } else {
                                cookbookAccess.setPermission(userUpdate.permission());
                        }
                }

                List<SharedUserDto> sharedUsers = cookbookAccessRepository
                                .findAllByCookbookIdOrderByGrantedAtAsc(cookbookId).stream()
                                .map(cookbookAccess -> new SharedUserDto(cookbookAccess.getUser().getId(),
                                                cookbookAccess.getUser().getDisplayUsername(),
                                                cookbookAccess.getUser().getAvatarUrl(),
                                                cookbookAccess.getPermission()))
                                .toList();
                return sharedUsers;
        }

        private Set<Long> getLikedRecipeIds(List<Long> recipeIds, Long userId) {
                if (userId == null)
                        return Set.of();
                return new HashSet<>(recipeLikeRepository.findLikedRecipeIds(recipeIds, userId));
        }
}
