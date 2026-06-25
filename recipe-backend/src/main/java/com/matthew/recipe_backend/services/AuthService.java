package com.matthew.recipe_backend.services;

import java.time.LocalDateTime;

import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.matthew.recipe_backend.dtos.AuthResponseDto;
import com.matthew.recipe_backend.dtos.LoginRequestDto;
import com.matthew.recipe_backend.dtos.RegisterRequestDto;
import com.matthew.recipe_backend.enums.UserRole;
import com.matthew.recipe_backend.exceptions.AccountDeactivatedException;
import com.matthew.recipe_backend.exceptions.EmailAlreadyExistsException;
import com.matthew.recipe_backend.exceptions.InvalidCredentialsException;
import com.matthew.recipe_backend.exceptions.InvalidTokenException;
import com.matthew.recipe_backend.exceptions.UsernameAlreadyExistsException;
import com.matthew.recipe_backend.models.AuthProvider;
import com.matthew.recipe_backend.models.Cookbook;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.repositories.AuthProviderRepository;
import com.matthew.recipe_backend.repositories.UserRepository;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthProviderRepository authProviderRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public AuthService(UserRepository userRepository, AuthProviderRepository authProviderRepository,
            PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.authProviderRepository = authProviderRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public AuthResponseDto register(RegisterRequestDto request) {
        // Check if email already exists
        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException("Email already in use");
        }

        // Check if username already exists
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException("Username already in use");
        }

        // Create user
        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(UserRole.USER);
        user.setDeactivated(false);
        user.setCreatedAt(LocalDateTime.now());
        User saved = userRepository.save(user);

        // Create local auth provider
        createAuthProvider(saved, "local", null);

        // Create default "Liked Recipes" cookbook
        // createDefaultCookbook(saved); MW: TO BE ADDED IN

        // Generate tokens
        return buildAuthResponse(saved);
    }

    public AuthResponseDto login(LoginRequestDto request) {
        // Check user exists
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new InvalidCredentialsException("Invalid email or password"));

        // Check account is not deactivated
        if (user.isDeactivated()) {
            throw new AccountDeactivatedException("This account has been deactivated");
        }

        // Check user has a local auth provider (not OAuth only)
        boolean hasLocalAuth = authProviderRepository
                .existsByUserAndProvider(user, "local");
        if (!hasLocalAuth) {
            throw new InvalidCredentialsException("Please sign in with Google");
        }

        // Validate password — throws AuthenticationException if invalid
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.email(),
                        request.password()));

        return buildAuthResponse(user);
    }

    public AuthResponseDto refresh(String refreshToken) {
        // Extract email from refresh token
        String email = jwtService.extractEmail(refreshToken);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new InvalidTokenException("Invalid refresh token"));

        // Validate the refresh token
        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new InvalidTokenException("Refresh token expired or invalid");
        }

        return buildAuthResponse(user);
    }

    public void logout(String token) {
        // If you add a token blacklist later, invalidate it here
        // For now JWT is stateless so logout is handled client-side
        // by discarding the token
    }

    // --- Private Helpers ---

    private void createAuthProvider(User user, String provider, String providerId) {
        AuthProvider authProvider = new AuthProvider();
        authProvider.setUser(user);
        authProvider.setProvider(provider);
        authProvider.setProviderId(providerId);
        authProviderRepository.save(authProvider);
    }

    // MW: TO BE ADDED
    // private void createDefaultCookbook(User user) {
    // Cookbook liked = new Cookbook();
    // liked.setName("Liked Recipes");
    // liked.setOwner(user);
    // liked.setDeleted(false);
    // liked.setCreatedAt(LocalDateTime.now());
    // cookbookRepository.save(liked);
    // }

    private AuthResponseDto buildAuthResponse(User user) {
        String accessToken = jwtService.generateToken(user);
        String refreshToken = jwtService.generateRefreshToken(user);

        return new AuthResponseDto(
                user.getId(),
                accessToken,
                refreshToken,
                user.getEmail(),
                user.getDisplayUsername(),
                user.getRole().name());
    }

    public User getOptionalCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }

        if (auth instanceof AnonymousAuthenticationToken) {
            return null;
        }

        Object principal = auth.getPrincipal();

        if (principal instanceof User user) {
            return user;
        }

        return null;
    }
}
