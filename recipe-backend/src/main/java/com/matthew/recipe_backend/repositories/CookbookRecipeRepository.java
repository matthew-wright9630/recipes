package com.matthew.recipe_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.keys.CookbookRecipeKey;
import com.matthew.recipe_backend.models.CookbookRecipe;

@Repository
public interface CookbookRecipeRepository extends JpaRepository<CookbookRecipe, CookbookRecipeKey> {

    boolean existsByRecipeIdAndCookbookId(Long recipeId, Long cookbookId);

    void deleteByRecipeIdAndCookbookId(Long recipeId, Long cookbookId);
}
