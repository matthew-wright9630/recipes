package com.matthew.recipe_backend.keys;

import java.io.Serializable;

import jakarta.persistence.Embeddable;

@Embeddable
public class RecipeLikeKey implements Serializable {
    private Long userId;
    private Long recipeId;
}
