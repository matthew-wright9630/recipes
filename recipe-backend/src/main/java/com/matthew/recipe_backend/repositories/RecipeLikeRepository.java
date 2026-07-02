package com.matthew.recipe_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.keys.RecipeLikeKey;
import com.matthew.recipe_backend.models.RecipeLike;

@Repository
public interface RecipeLikeRepository extends JpaRepository<RecipeLike, RecipeLikeKey> {

        @Query("""
                        SELECT rl.recipe.id, COUNT(rl)
                        FROM RecipeLike rl
                        WHERE rl.recipe.id IN :recipeIds
                        GROUP BY rl.recipe.id
                        """)
        List<Object[]> countLikesByRecipeIds(@Param("recipeIds") List<Long> recipeIds);

        @Query("""
                        SELECT rl.recipe.id
                        FROM RecipeLike rl
                        WHERE rl.recipe.id IN :recipeIds
                        AND rl.user.id = :userId
                        """)
        List<Long> findLikedRecipeIds(@Param("recipeIds") List<Long> recipeIds, @Param("userId") Long userId);

        boolean existsByRecipeIdAndUserId(Long recipeId, Long userId);

        RecipeLike deleteByRecipeIdAndUserId(Long recipeId, Long userId);
}
