package com.matthew.recipe_backend.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matthew.recipe_backend.models.Ingredient;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {

    Optional<Ingredient> findByNormalizedName(String name);
}
