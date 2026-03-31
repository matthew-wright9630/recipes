package com.matthew.recipe_backend.services;

import java.util.List;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.Utils.CustomUserDetails;
import com.matthew.recipe_backend.exceptions.UserNotFoundException;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.UserRepository;

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
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        return new CustomUserDetails(
                user.getId(),
                user.getUsername(),
                user.getPasswordHash(),
                List.of(new SimpleGrantedAuthority(user.getRole().name())));
    }
}
