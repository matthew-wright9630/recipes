package com.matthew.recipe_backend.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.models.AuthProvider;
import com.matthew.recipe_backend.models.User;

@Repository
public interface AuthProviderRepository extends JpaRepository<AuthProvider, Long> {
    boolean existsByUserAndProvider(User user, String provider);

    Optional<AuthProvider> findByProviderAndProviderId(String provider, String providerId);

    List<AuthProvider> findByUser(User user);
}
