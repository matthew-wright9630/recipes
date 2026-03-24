package com.matthew.recipe_backend.dtos;

import java.math.BigDecimal;

public record RecipeIngredientDto(String name, BigDecimal quantity, String unit) {
}
