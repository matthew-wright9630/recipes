package com.matthew.recipe_backend.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.models.UserImage;

@Repository
public interface UserImageRepository extends JpaRepository<UserImage, Long> {
    List<UserImage> findByUserOrderByCreatedAtDesc(User user);
}
