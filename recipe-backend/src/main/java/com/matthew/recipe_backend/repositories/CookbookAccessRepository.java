package com.matthew.recipe_backend.repositories;

import java.util.Collection;
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
            AND ca.cookbook.deleted = false
      """)
  Page<Cookbook> findCookbooksByUserAndPermissions(
      @Param("user") User user,
      @Param("permissions") List<CookbookPermission> permissions, Pageable pageable);

  @Query("""
          SELECT ca.cookbook
          FROM CookbookAccess ca
          WHERE ca.user.id = :userId
            AND ca.permission IN :permissions
            AND ca.cookbook.deleted = false
            AND LOWER(ca.cookbook.name) LIKE LOWER(CONCAT('%', :name, '%'))
      """)
  Page<Cookbook> findCookbooksByUserAndPermissionsAndNameContainingIgnoreCase(@Param("userId") Long userId,
      @Param("permissions") List<CookbookPermission> permissions, Pageable pageable, String name);

  @Query("""
          SELECT ca.cookbook
          FROM CookbookAccess ca
          WHERE ca.user.id = :userId
          AND ca.permission IN :permissions
            AND ca.cookbook.deleted = false
      """)
  List<Cookbook> findCookbooksByUserIdAndPermissionIn(
      @Param("userId") Long userId,
      @Param("permissions") Collection<CookbookPermission> permissions);

  boolean existsByCookbookIdAndUserIdAndPermissionIn(
      Long cookbookId,
      Long userId,
      Collection<CookbookPermission> permissions);

  @Query("""
          SELECT ca.permission
          FROM CookbookAccess ca
          WHERE ca.cookbook.id = :cookbookId
            AND ca.user.id = :userId
      """)
  CookbookPermission findPermissionByCookbookIdAndUserId(
      @Param("cookbookId") Long cookbookId,
      @Param("userId") Long userId);

  List<CookbookAccess> findAllByCookbookId(Long cookbookId);

  List<CookbookAccess> findAllByCookbookIdAndPermissionNot(
      Long cookbookId,
      CookbookPermission permission);

  boolean existsByCookbookIdAndUserId(Long cookbookId, Long userId);
}
