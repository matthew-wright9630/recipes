package com.matthew.recipe_backend.repositories;

import java.util.List;
import java.util.Set;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.enums.RecipeStatus;
import com.matthew.recipe_backend.keys.CookbookRecipeKey;
import com.matthew.recipe_backend.models.CookbookRecipe;

@Repository
public interface CookbookRecipeRepository extends JpaRepository<CookbookRecipe, CookbookRecipeKey> {

        boolean existsByRecipeIdAndCookbookId(Long recipeId, Long cookbookId);

        void deleteByRecipeIdAndCookbookId(Long recipeId, Long cookbookId);

        @Query("""
                            SELECT cr.id.cookbookId
                            FROM CookbookRecipe cr
                            WHERE cr.id.recipeId = :recipeId
                        """)
        Set<Long> findCookbookIdsByRecipeId(Long recipeId);

        @Query("""
                        SELECT cr
                        FROM CookbookRecipe cr
                        JOIN FETCH cr.recipe r
                        WHERE cr.cookbook.id = :cookbookId
                          AND r.status = :status
                        """)
        List<CookbookRecipe> findByCookbookIdAndStatus(
                        @Param("cookbookId") Long cookbookId,
                        @Param("status") RecipeStatus status);

        @Modifying
        @Query(value = """
                        UPDATE cookbook_recipes
                        SET recipe_id = :newRecipeId
                        WHERE recipe_id = :oldRecipeId
                        """, nativeQuery = true)
        void moveRecipes(Long oldRecipeId, Long newRecipeId);

        boolean existsByCookbookIdAndRecipeId(Long cookbookId, Long recipeId);

        void deleteByCookbookIdAndRecipeId(Long cookbookId, Long recipeId);
}
