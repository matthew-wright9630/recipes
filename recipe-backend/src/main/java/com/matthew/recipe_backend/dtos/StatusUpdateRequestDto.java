package com.matthew.recipe_backend.dtos;

import com.matthew.recipe_backend.enums.RecipeStatus;

public class StatusUpdateRequestDto {
    private RecipeStatus status;

    public RecipeStatus getStatus() {
        return status;
    }

    public void setStatus(RecipeStatus status) {
        this.status = status;
    }
}
