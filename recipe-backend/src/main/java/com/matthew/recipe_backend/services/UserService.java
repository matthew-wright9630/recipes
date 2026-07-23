package com.matthew.recipe_backend.services;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.data.domain.Page;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.matthew.recipe_backend.dtos.ChangePasswordDto;
import com.matthew.recipe_backend.dtos.UserDto;
import com.matthew.recipe_backend.dtos.UserUpdateDto;
import com.matthew.recipe_backend.exceptions.InvalidCredentialsException;
import com.matthew.recipe_backend.exceptions.InvalidRequestException;
import com.matthew.recipe_backend.exceptions.UserNotFoundException;
import com.matthew.recipe_backend.exceptions.UsernameAlreadyExistsException;
import com.matthew.recipe_backend.mappers.UserMapper;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    public UserDetails loadUserByUsername(String username) {

        return userRepository.findByEmail(username.toLowerCase())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }

    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(UserMapper::toDto);
    }

    public UserDto getCurrentUser(User user) {
        return UserMapper.toDto(user);
    }

    public UserDto updateUser(User user, UserUpdateDto updateUserDto) {
        User foundUser = userRepository.findById(user.getId())
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        // Check if username is already taken by another user
        if (!foundUser.getDisplayUsername().equals(updateUserDto.username()) &&
                userRepository.existsByUsername(updateUserDto.username())) {
            throw new UsernameAlreadyExistsException("Username is already in use");
        }

        foundUser.setUsername(updateUserDto.username());

        User savedUser = userRepository.save(foundUser);
        return UserMapper.toDto(savedUser);
    }

    public UserDto createUser(User user) {
        user.setEmail(user.getEmail().toLowerCase());
        User saved = userRepository.save(user);
        UserDto response = UserMapper.toDto(saved);

        // Cookbook likedRecipes = new Cookbook();
        // likedRecipes.setName("Liked Recipes");
        // likedRecipes.setOwner(saved);
        // cookbookRepository.save(likedRecipes);

        return response;
    }
}
