package com.matthew.recipe_backend.services;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.CookbookDetailsDto;
import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.exceptions.UserNotFoundException;
import com.matthew.recipe_backend.mappers.CookbookMapper;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.CookbookAccess;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.CookbookAccessRepository;
import com.matthew.recipe_backend.repositories.CookbookRepository;
import com.matthew.recipe_backend.repositories.UserRepository;
import com.matthew.recipe_backend.validators.CookbookValidator;

import jakarta.persistence.EntityNotFoundException;
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

        // public List<CookbookDto> findMyCookbooks(String username) {
        // User user = userRepository.findByEmail(username.toLowerCase())
        // .orElseThrow(() -> new UserNotFoundException(username));

        // return cookbookAccessRepository
        // .findCookbooksByUserAndPermissions(
        // user,
        // List.of(
        // CookbookPermission.OWNER))
        // .stream()
        // .map(CookbookMapper::toDto)
        // .toList();
        // }

        // public List<CookbookDto> findSharedCookbooks(String username) {
        // User user = userRepository.findByEmail(username.toLowerCase())
        // .orElseThrow(() -> new UserNotFoundException(username));

        // return cookbookAccessRepository
        // .findCookbooksByUserAndPermissions(
        // user,
        // List.of(
        // CookbookPermission.READ,
        // CookbookPermission.READ_WRITE))
        // .stream()
        // .map(CookbookMapper::toDto)
        // .toList();
        // }

        public Page<CookbookDto> findAllAccessibleCookbooks(Pageable pageable, String search, User user) {
                return cookbookAccessRepository
                                .findCookbooksByUserAndPermissions(
                                                user,
                                                List.of(
                                                                CookbookPermission.OWNER,
                                                                CookbookPermission.READ,
                                                                CookbookPermission.READ_WRITE),
                                                pageable)
                                .map(CookbookMapper::toDto);
        }

        @Transactional
        public CookbookDto createCookbook(String username, CookbookDetailsDto cookbookCreationDto) {
                User user = userRepository.findByEmail(username.toLowerCase())
                                .orElseThrow(() -> new UserNotFoundException(username));

                Cookbook cookbook = new Cookbook(cookbookCreationDto.name(), cookbookCreationDto.description(),
                                cookbookCreationDto.imageUrl());
                cookbookRepository.save(cookbook);
                CookbookAccess access = new CookbookAccess(cookbook, user, CookbookPermission.OWNER, Instant.now());
                cookbookAccessRepository.save(access);

                return CookbookMapper.toDto(cookbook);
        }

        @Transactional
        public CookbookDto updateCookbook(Long id, String username, CookbookDetailsDto cookbookDetailsDto) {
                User user = userRepository.findByEmail(username.toLowerCase())
                                .orElseThrow(() -> new UserNotFoundException(username));

                Cookbook foundCookbook = cookbookRepository.findById(id)
                                .orElseThrow(() -> new EntityNotFoundException("Cookbook not found"));

                CookbookValidator.assertUserOwnsCookbook(cookbookAccessRepository, id, user.getId());

                foundCookbook.setName(cookbookDetailsDto.name());
                foundCookbook.setDescription(cookbookDetailsDto.description());
                foundCookbook.setImageUrl(cookbookDetailsDto.imageUrl());

                Cookbook savedCookbook = cookbookRepository.save(foundCookbook);
                return CookbookMapper.toDto(savedCookbook);
        }

}
