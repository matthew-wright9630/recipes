package com.matthew.recipe_backend.dtos;

import java.math.BigDecimal;

public record RecipeIngredientDto(Long id, String name, BigDecimal quantity, String unit) {
}
