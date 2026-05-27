package com.matthew.recipe_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matthew.recipe_backend.keys.RecipeLikeKey;
import com.matthew.recipe_backend.models.RecipeLike;

public interface RecipeLikeRepository extends JpaRepository<RecipeLike, RecipeLikeKey> {
    long countByIdRecipeId(Integer recipeId);

    boolean existsByIdRecipeIdAndIdUserId(Integer recipeId, Integer userId);
}
