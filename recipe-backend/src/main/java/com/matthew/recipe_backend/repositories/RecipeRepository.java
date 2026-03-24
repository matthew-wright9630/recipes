package com.matthew.recipe_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.matthew.recipe_backend.models.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	@Query("""
			    SELECT DISTINCT r FROM Recipe r
			    LEFT JOIN FETCH r.recipeIngredients ri
			    LEFT JOIN FETCH ri.ingredient
			    WHERE r.id = :id
			""")
	Optional<Recipe> findByIdWithIngredients(@Param("id") Long id);

	@Query("""
			    SELECT DISTINCT r FROM Recipe r
			    LEFT JOIN FETCH r.recipeIngredients ri
			    LEFT JOIN FETCH ri.ingredient
			""")
	List<Recipe> findAllWithIngredients();

}
