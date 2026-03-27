package com.matthew.recipe_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.models.RecipeDirection;

@Repository
public interface RecipeDirectionRepository extends JpaRepository<RecipeDirection, Long> {

}
