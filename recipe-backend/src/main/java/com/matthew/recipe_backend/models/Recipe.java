package com.matthew.recipe_backend.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column
	private String description;

	@Column
	private String notes;

	@Column
	private int servings;

	@Column(name = "prep_time")
	private int prepTime;

	@Column(name = "cook_time")
	private int cookTime;

	@Column(name = "created_by")
	private long createdBy;

	@Column(nullable = true)
	private Boolean active;

	@Column(nullable = false)
	private int version;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Column
	private Boolean published;

	@OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<RecipeDirections> recipeDirections;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_recipe_id")
	private Recipe parentRecipe;

	@OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
	private List<RecipeIngredient> recipeIngredients;

	protected Recipe() { // for JPA use

	}

	public Recipe(long createdBy, String name) {
		this.name = name;
		this.description = null;
		this.notes = null;
		this.servings = 0;
		this.prepTime = 0;
		this.cookTime = 0;
		this.createdBy = createdBy;
		this.active = true;
		this.version = 0;
		this.createdAt = OffsetDateTime.now();
		this.published = false;
		this.recipeDirections = null;
		this.recipeIngredients = null;
	}

	public Recipe(String name, String description, String notes, int servings, int prepTime, int cookTime,
			long createdBy, Boolean active, int version, OffsetDateTime createdAt, Boolean published,
			List<RecipeDirections> recipeDirections, List<RecipeIngredient> recipeIngredients) {
		this.name = name;
		this.description = description;
		this.notes = notes;
		this.servings = servings;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.createdBy = createdBy;
		this.active = active;
		this.version = version;
		this.createdAt = createdAt;
		this.published = published;
		this.recipeDirections = recipeDirections;
		this.recipeIngredients = recipeIngredients;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getNotes() {
		return notes;
	}

	public void setNotes(String notes) {
		this.notes = notes;
	}

	public int getServings() {
		return servings;
	}

	public void setServings(int servings) {
		this.servings = servings;
	}

	public int getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(int prepTime) {
		this.prepTime = prepTime;
	}

	public int getCookTime() {
		return cookTime;
	}

	public void setCookTime(int cookTime) {
		this.cookTime = cookTime;
	}

	public long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(long createdBy) {
		this.createdBy = createdBy;
	}

	public Boolean getPublished() {
		return published;
	}

	public void setPublished(Boolean published) {
		this.published = published;
	}

	public Boolean getActive() {
		return active;
	}

	public void setActive(Boolean active) {
		this.active = active;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion(int version) {
		this.version = version;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<RecipeDirections> getRecipeDirections() {
		return recipeDirections;
	}

	public void setRecipeDirections(List<RecipeDirections> recipeDirections) {
		this.recipeDirections = recipeDirections;
	}

	public Recipe getParentRecipe() {
		return parentRecipe;
	}

	public void setParentRecipe(Recipe parentRecipe) {
		this.parentRecipe = parentRecipe;
	}

	public List<RecipeIngredient> getRecipeIngredients() {
		return recipeIngredients;
	}

	public void setRecipeIngredients(List<RecipeIngredient> recipeIngredients) {
		this.recipeIngredients = recipeIngredients;
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
		Recipe other = (Recipe) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Recipe [id=" + id + ", name=" + name + ", description=" + description + ", notes=" + notes
				+ ", servings=" + servings + ", prepTime=" + prepTime + ", cookTime=" + cookTime + ", createdBy="
				+ createdBy + ", active=" + active + ", version=" + version + ", createdAt=" + createdAt
				+ ", published=" + published + ", recipeDirections=" + recipeDirections + ", recipeIngredients="
				+ recipeIngredients + "]";
	}

}
