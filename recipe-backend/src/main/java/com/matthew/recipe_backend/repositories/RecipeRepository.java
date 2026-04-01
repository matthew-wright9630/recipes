package com.matthew.recipe_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.matthew.recipe_backend.models.Recipe;

/**
 * Repository for {@link Recipe} entities, extending Spring Data JPA's standard
 * CRUD operations.
 *
 * <p>
 * Custom queries use {@code LEFT JOIN FETCH} to eagerly load associations in a
 * single
 * query, avoiding N+1 select problems. {@code DISTINCT} is required to prevent
 * duplicate
 * root entities in the result set caused by the join.
 *
 * <p>
 * Directions and ingredients are never fetched together in the same query, as
 * fetching
 * two collection associations simultaneously would produce a Cartesian product.
 * Where both
 * are needed (e.g. {@code findRecipeById}), two separate queries are issued
 * instead.
 */
@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {

	@Query("""
			    SELECT DISTINCT r FROM Recipe r
			    LEFT JOIN FETCH r.recipeDirections rd
			    LEFT JOIN FETCH rd.recipeIngredients rdi
			    WHERE r.id = :id
			""")
	Optional<Recipe> findByIdWithDirections(@Param("id") Long id);

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

	@Query("""
			    SELECT DISTINCT r FROM Recipe r
			    LEFT JOIN FETCH r.recipeDirections rd
			    LEFT JOIN FETCH rd.recipeIngredients rdi
			""")
	List<Recipe> findAllWithDirections();

}
