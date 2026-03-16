package com.matthew.recipe_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class IngredientNutritionMap {

    @Id
    private long id;

    @ManyToOne
    @MapsId("ingredientId")
    private Ingredients ingredient;

    @ManyToOne
    @MapsId("usdaId")
    private UsdaIngredients usdaIngredients;

    public IngredientNutritionMap() {
    }

    public IngredientNutritionMap(Ingredients ingredient, UsdaIngredients usdaIngredients) {
        this.ingredient = ingredient;
        this.usdaIngredients = usdaIngredients;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Ingredients getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredients ingredient) {
        this.ingredient = ingredient;
    }

    public UsdaIngredients getUsdaIngredients() {
        return usdaIngredients;
    }

    public void setUsdaIngredients(UsdaIngredients usdaIngredients) {
        this.usdaIngredients = usdaIngredients;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (id ^ (id >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        IngredientNutritionMap other = (IngredientNutritionMap) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "IngredientNutritionMap [id=" + id + "]";
    }
}
