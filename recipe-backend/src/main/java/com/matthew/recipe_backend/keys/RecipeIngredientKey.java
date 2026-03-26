package com.matthew.recipe_backend.keys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RecipeIngredientKey implements Serializable {

	@Column(name = "recipe_id")
	private Long recipeId;

	@Column(name = "ingredient_id")
	private Long ingredientId;

	public RecipeIngredientKey() {
	}

	public RecipeIngredientKey(Long recipeId, Long ingredientId) {
		this.recipeId = recipeId;
		this.ingredientId = ingredientId;
	}

	public Long getRecipeId() {
		return recipeId;
	}

	public void setRecipeId(Long recipeId) {
		this.recipeId = recipeId;
	}

	public Long getIngredientId() {
		return ingredientId;
	}

	public void setIngredientId(Long ingredientId) {
		this.ingredientId = ingredientId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(recipeId, ingredientId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		RecipeIngredientKey other = (RecipeIngredientKey) obj;
		return Objects.equals(recipeId, other.recipeId) && Objects.equals(ingredientId, other.ingredientId);
	}

	@Override
	public String toString() {
		return "RecipeIngredientKey [recipeId=" + recipeId + ", ingredientId=" + ingredientId + "]";
	}

}
