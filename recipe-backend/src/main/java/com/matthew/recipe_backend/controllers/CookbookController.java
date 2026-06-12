package com.matthew.recipe_backend.controllers;

import org.springframework.web.bind.annotation.RestController;

import com.matthew.recipe_backend.dtos.CookbookDetailsDto;
import com.matthew.recipe_backend.dtos.CookbookDto;
import com.matthew.recipe_backend.models.User;
import com.matthew.recipe_backend.services.CookbookService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
public class CookbookController {

    private final CookbookService cookbookService;

    public CookbookController(CookbookService cookbookService) {
        this.cookbookService = cookbookService;
    }

    @GetMapping("/owned")
    public ResponseEntity<List<CookbookDto>> getMyCookbooks(@AuthenticationPrincipal User user) {
        List<CookbookDto> cookbooks = cookbookService.findMyCookbooks(user.getUsername());
        return ResponseEntity.ok(cookbooks);
    }

    @GetMapping("/shared")
    public ResponseEntity<List<CookbookDto>> getAllSharedCookbooks(@AuthenticationPrincipal User user) {
        List<CookbookDto> cookbooks = cookbookService.findSharedCookbooks(user.getUsername());
        return ResponseEntity.ok(cookbooks);
    }

    @GetMapping("/accessible")
    public ResponseEntity<List<CookbookDto>> getAllAccessibleCookbooks(@AuthenticationPrincipal User user) {
        List<CookbookDto> cookbooks = cookbookService.findAllAccessibleCookbooks(user.getUsername());
        return ResponseEntity.ok(cookbooks);
    }

    @PostMapping
    public ResponseEntity<CookbookDto> postCookbook(@AuthenticationPrincipal User user,
            @RequestBody CookbookDetailsDto cookbookDto) {
        CookbookDto cookbook = cookbookService.createCookbook(user.getUsername(), cookbookDto);
        return ResponseEntity.ok(cookbook);
    }

}
