package com.matthew.recipe_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.AddRecipeDto;
import com.matthew.recipe_backend.dtos.CookbookDetailsDto;
import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.dtos.CookbookRecipeSelectionDto;
import com.matthew.recipe_backend.dtos.CookbookWithRecipesDto;
import com.matthew.recipe_backend.dtos.CookbookRecipeDto;
import com.matthew.recipe_backend.dtos.CreateCookbookDto;
import com.matthew.recipe_backend.dtos.RecipeDto;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.services.CookbookService;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api/cookbooks")
public class CookbookController {

    private final CookbookService cookbookService;

    public CookbookController(CookbookService cookbookService) {
        this.cookbookService = cookbookService;
    }

    // @GetMapping("/owned")
    // public ResponseEntity<List<CookbookDto>>
    // getMyCookbooks(@AuthenticationPrincipal User user) {
    // List<CookbookDto> cookbooks =
    // cookbookService.findMyCookbooks(user.getUsername());
    // return ResponseEntity.ok(cookbooks);
    // }

    // @GetMapping("/shared")
    // public ResponseEntity<List<CookbookDto>>
    // getAllSharedCookbooks(@AuthenticationPrincipal User user) {
    // List<CookbookDto> cookbooks =
    // cookbookService.findSharedCookbooks(user.getUsername());
    // return ResponseEntity.ok(cookbooks);
    // }

    @GetMapping("/{id}")
    public ResponseEntity<CookbookWithRecipesDto> getCookbookById(@AuthenticationPrincipal User user,
            @PathVariable Long id) {
        CookbookWithRecipesDto cookbook = cookbookService.findCookbookById(user, id);
        return ResponseEntity.ok(cookbook);
    }

    @GetMapping("/accessible")
    public ResponseEntity<Page<CookbookDto>> getAllAccessibleCookbooks(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(defaultValue = "") String search,
            @AuthenticationPrincipal User user) {
        Pageable pageable = PageRequest.of(page, size);
        Page<CookbookDto> cookbooks = cookbookService.findAllAccessibleCookbooks(pageable, search, user);
        return ResponseEntity.ok(cookbooks);
    }

    @GetMapping("/list")
    public ResponseEntity<List<CookbookRecipeSelectionDto>> getListOfCookbooks(@AuthenticationPrincipal User user,
            @RequestParam Long recipeId) {
        List<CookbookRecipeSelectionDto> cookbooks = cookbookService.findAllEditableCookbooks(user, recipeId);
        return ResponseEntity.ok(cookbooks);
    }

    @PostMapping
    public ResponseEntity<CookbookDto> postCookbook(@AuthenticationPrincipal User user,
            @RequestBody CreateCookbookDto cookbookDto) {
        CookbookDto cookbook = cookbookService.createCookbook(user.getUsername(), cookbookDto);
        return ResponseEntity.ok(cookbook);
    }

    @PostMapping("/{cookbookId}/recipes")
    public ResponseEntity<CookbookDto> postCookbookRecipe(@AuthenticationPrincipal User user,
            @PathVariable Long cookbookId, @RequestBody AddRecipeDto addRecipeDto) {
        CookbookDto cookbook = cookbookService.addRecipeToCookbook(user.getUsername(), cookbookId, addRecipeDto);
        return ResponseEntity.ok(cookbook);
    }

    @PutMapping("/{cookbookId}")
    public ResponseEntity<CookbookDto> updateCookbook(@AuthenticationPrincipal User user,
            @PathVariable Long cookbookId, @RequestBody CreateCookbookDto cookbookDto) {
        CookbookDto cookbook = cookbookService.editCookbook(user, cookbookId, cookbookDto);
        return ResponseEntity.ok(cookbook);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteCookbook(@PathVariable long id,
            @AuthenticationPrincipal User user) {
        cookbookService.removeCookbook(user, id);
        return ResponseEntity.noContent().build();
    }

}
