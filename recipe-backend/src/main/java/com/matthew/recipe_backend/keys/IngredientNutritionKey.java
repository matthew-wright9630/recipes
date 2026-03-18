package com.matthew.recipe_backend.keys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class IngredientNutritionKey implements Serializable {

    private Long ingredientId;
    private Long usdaId;

    public IngredientNutritionKey() {}

    public IngredientNutritionKey(Long ingredientId, Long usdaId) {
        this.ingredientId = ingredientId;
        this.usdaId = usdaId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof IngredientNutritionKey)) return false;
        IngredientNutritionKey that = (IngredientNutritionKey) o;
        return Objects.equals(ingredientId, that.ingredientId) &&
               Objects.equals(usdaId, that.usdaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredientId, usdaId);
    }
}
