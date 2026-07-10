package com.matthew.recipe_backend.repositories;

import java.time.Instant;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.models.Recipe;
import com.matthew.recipe_backend.models.RecipeView;
import com.matthew.recipe_backend.models.User;

@Repository
public interface RecipeViewRepository extends JpaRepository<RecipeView, Long> {

        boolean existsByUserAndRecipeAndCreatedAtAfter(
                        User user,
                        Recipe recipe,
                        Instant createdAt);

        boolean existsByRecipeAndVisitorIdAndCreatedAtAfter(
                        Recipe recipe,
                        String visitorId,
                        Instant createdAt);

        @Query("""
                            SELECT rv
                            FROM RecipeView rv
                            WHERE rv.user = :user
                            ORDER BY rv.createdAt DESC
                        """)
        List<RecipeView> findRecentViewsByUser(
                        @Param("user") User user,
                        Pageable pageable);

        @Query(value = """
                        SELECT r.* FROM recipes r
                        INNER JOIN recipe_views rh ON rh.recipe_id = r.id
                        WHERE rh.user_id = :userId
                        GROUP BY r.id
                        ORDER BY MAX(rh.viewed_at) DESC
                        LIMIT :limit
                        """, nativeQuery = true)
        List<Recipe> findDistinctRecentlyViewedRecipes(@Param("userId") Long userId, @Param("limit") int limit);
}
