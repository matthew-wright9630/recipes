package com.matthew.recipe_backend.enums;

public enum RecipeStatus {
    DRAFT,
    PUBLISHED,
    ARCHIVED, // Used for when a user edits a recipe or otherwise wants to delete a recipe
    REMOVED // Used for moderation or copyright issues
}
