package com.matthew.recipe_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.util.Objects;

import com.matthew.recipe_backend.keys.RecipeIngredientKey;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient {

	@EmbeddedId
	private RecipeIngredientKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("recipeId")
	@JoinColumn(name = "recipe_id")
	private Recipe recipe;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("ingredientId")
	@JoinColumn(name = "ingredient_id")
	private Ingredient ingredient;

	@Column(name = "quantity")
	private BigDecimal quantity;

	@Column(name = "unit")
	private String unit;

	public RecipeIngredient() {
	}

	public RecipeIngredient(Recipe recipe, Ingredient ingredient, BigDecimal quantity, String unit) {
		this.recipe = recipe;
		this.ingredient = ingredient;
		this.quantity = quantity;
		this.unit = unit;
		if (recipe != null && ingredient != null) {
			this.id = new RecipeIngredientKey((int) recipe.getId(), (int) ingredient.getId());
		}
	}

	public RecipeIngredientKey getId() {
		return id;
	}

	public void setId(RecipeIngredientKey id) {
		this.id = id;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Ingredient getIngredient() {
		return ingredient;
	}

	public void setIngredient(Ingredient ingredient) {
		this.ingredient = ingredient;
	}

	public BigDecimal getQuantity() {
		return quantity;
	}

	public void setQuantity(BigDecimal quantity) {
		this.quantity = quantity;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecipeIngredient other = (RecipeIngredient) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "RecipeIngredients [id=" + id + ", quantity=" + quantity + ", unit=" + unit + "]";
	}

}
