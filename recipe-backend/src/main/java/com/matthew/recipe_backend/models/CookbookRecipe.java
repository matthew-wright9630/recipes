package com.matthew.recipe_backend.models;

import java.time.Instant;

import com.matthew.recipe_backend.keys.CookbookRecipeKey;

import jakarta.persistence.Entity;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;

@Entity
public class CookbookRecipe {

	@EmbeddedId
	private CookbookRecipeKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("cookbookId")
	private Cookbook cookbook;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("recipeId")
	private Recipe recipe;

	@Column(name = "added_at")
	private Instant addedAt;

	public CookbookRecipe() {
	}

	public CookbookRecipe(Cookbook cookbook, Recipe recipe, Instant addedAt) {
		this.cookbook = cookbook;
		this.recipe = recipe;
		this.addedAt = addedAt;
		if (cookbook != null && recipe != null) {
			this.id = new CookbookRecipeKey(cookbook.getId(), recipe.getId());
		}
	}

	public CookbookRecipeKey getId() {
		return id;
	}

	public void setId(CookbookRecipeKey id) {
		this.id = id;
	}

	public Cookbook getCookbook() {
		return cookbook;
	}

	public void setCookbook(Cookbook cookbook) {
		this.cookbook = cookbook;
	}

	public Recipe getRecipe() {
		return recipe;
	}

	public void setRecipe(Recipe recipe) {
		this.recipe = recipe;
	}

	public Instant getAddedAt() {
		return addedAt;
	}

	public void setAddedAt(Instant addedAt) {
		this.addedAt = addedAt;
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
		CookbookRecipe other = (CookbookRecipe) obj;
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
		return "CookbookRecipe [id=" + id + ", addedAt=" + addedAt + "]";
	}

}
