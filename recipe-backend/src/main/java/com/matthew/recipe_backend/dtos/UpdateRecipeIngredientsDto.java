package com.matthew.recipe_backend.dtos;

import java.math.BigDecimal;

public record UpdateRecipeIngredientsDto(String name,
                BigDecimal quantity,
                String unit,
                String notes) {

}
