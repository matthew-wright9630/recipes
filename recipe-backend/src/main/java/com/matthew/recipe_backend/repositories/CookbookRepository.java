package com.matthew.recipe_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.matthew.recipe_backend.enums.CookbookType;
import com.matthew.recipe_backend.models.Cookbook;

public interface CookbookRepository extends JpaRepository<Cookbook, Long> {

    Cookbook findByOwner_IdAndType(Long ownerId, CookbookType type);

    boolean existsByOwner_IdAndName(Long ownerId, String name);

    boolean existsByOwner_IdAndNameAndIdNot(Long ownerId, String name, Long id);
}
