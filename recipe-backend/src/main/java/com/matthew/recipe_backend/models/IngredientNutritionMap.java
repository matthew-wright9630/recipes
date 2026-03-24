package com.matthew.recipe_backend.models;

import com.matthew.recipe_backend.keys.IngredientNutritionKey;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredient_nutrition_map")
public class IngredientNutritionMap {

	@EmbeddedId
	private IngredientNutritionKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("ingredientId")
	private Ingredient ingredient;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("usdaId")
	private UsdaIngredient usdaIngredients;

	public IngredientNutritionMap() {
	}

	public IngredientNutritionMap(Ingredient ingredient, UsdaIngredient usdaIngredients) {
		this.ingredient = ingredient;
		this.usdaIngredients = usdaIngredients;
	}

	public IngredientNutritionKey getId() {
		return id;
	}

	public void setId(IngredientNutritionKey id) {
		this.id = id;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public UsdaIngredient getUsdaIngredients() {
		return usdaIngredients;
	}

	public void setUsdaIngredients(UsdaIngredient usdaIngredients) {
		this.usdaIngredients = usdaIngredients;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		}
		else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "IngredientNutritionMap [id=" + id + "]";
	}

}
