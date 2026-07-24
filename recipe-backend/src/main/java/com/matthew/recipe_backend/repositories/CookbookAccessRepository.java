package com.matthew.recipe_backend.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.keys.CookbookAccessKey;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.CookbookAccess;
import com.matthew.recipe_backend.models.User;

public interface CookbookAccessRepository extends JpaRepository<CookbookAccess, CookbookAccessKey> {

    @Query("""
                SELECT ca.cookbook
                FROM CookbookAccess ca
                WHERE ca.user = :user
                  AND ca.permission IN :permissions
            """)
    Page<Cookbook> findCookbooksByUserAndPermissions(
            @Param("user") User user,
            @Param("permissions") List<CookbookPermission> permissions, Pageable pageable);

    boolean existsByCookbookIdAndUserIdAndPermission(
            Long cookbookId,
            Long userId,
            CookbookPermission permission);
}
