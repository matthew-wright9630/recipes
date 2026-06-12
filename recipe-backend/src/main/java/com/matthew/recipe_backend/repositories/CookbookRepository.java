package com.matthew.recipe_backend.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.User;

import java.util.List;

@Repository
public interface CookbookRepository extends JpaRepository<Cookbook, Long> {

    List<Cookbook> findByOwner(User owner);
}
