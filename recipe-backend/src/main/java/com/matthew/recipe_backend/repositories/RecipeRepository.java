package com.matthew.recipe_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.matthew.recipe_backend.models.Recipe;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    
}
