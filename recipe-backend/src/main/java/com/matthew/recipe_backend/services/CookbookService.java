package com.matthew.recipe_backend.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.exceptions.UserNotFoundException;
import com.matthew.recipe_backend.mappers.CookbookMapper;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.CookbookAccessRepository;
import com.matthew.recipe_backend.repositories.CookbookRepository;
import com.matthew.recipe_backend.repositories.UserRepository;

import jakarta.transaction.Transactional;

@Service
@Transactional
public class CookbookService {

    private final CookbookRepository cookbookRepository;
    private final CookbookAccessRepository cookbookAccessRepository;
    private final UserRepository userRepository;

    public CookbookService(CookbookRepository cookbookRepository, UserRepository userRepository,
            CookbookAccessRepository cookbookAccessRepository) {
        this.cookbookRepository = cookbookRepository;
        this.userRepository = userRepository;
        this.cookbookAccessRepository = cookbookAccessRepository;
    }

    public List<CookbookDto> findMyCookbooks(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return cookbookAccessRepository
                .findCookbooksByUserAndPermissions(
                        user,
                        List.of(
                                CookbookPermission.OWNER))
                .stream()
                .map(CookbookMapper::toDto)
                .toList();
    }

    public List<CookbookDto> findSharedCookbooks(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return cookbookAccessRepository
                .findCookbooksByUserAndPermissions(
                        user,
                        List.of(
                                CookbookPermission.READ,
                                CookbookPermission.READ_WRITE))
                .stream()
                .map(CookbookMapper::toDto)
                .toList();
    }

    public List<CookbookDto> findAllAccessibleCookbooks(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        return cookbookAccessRepository
                .findCookbooksByUserAndPermissions(
                        user,
                        List.of(
                                CookbookPermission.OWNER,
                                CookbookPermission.READ,
                                CookbookPermission.READ_WRITE))
                .stream()
                .map(CookbookMapper::toDto)
                .toList();
    }
}
