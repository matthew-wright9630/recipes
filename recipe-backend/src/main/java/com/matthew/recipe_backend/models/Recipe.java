package com.matthew.recipe_backend.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.Set;

import com.matthew.recipe_backend.enums.RecipeStatus;

import java.util.Objects;

@Entity
@Table(name = "recipes")
public class Recipe {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false)
	private String name;

	@Column
	private String description;

	@Column
	private String notes;

	@Column
	private Integer servings;

	@Column(name = "prep_time")
	private Integer prepTime;

	@Column(name = "cook_time")
	private Integer cookTime;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by", referencedColumnName = "id")
	private User createdBy;

	@Column(nullable = false)
	private Integer version;

	@Column(name = "created_at", nullable = false)
	private OffsetDateTime createdAt;

	@Enumerated(EnumType.STRING)
	@Column(columnDefinition = "recipe_status")
	private RecipeStatus status;

	@OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<RecipeDirection> recipeDirections;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "parent_recipe_id")
	private Recipe parentRecipe;

	@OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
	private Set<RecipeIngredient> recipeIngredients;

	protected Recipe() { // for JPA use

	}

	public Recipe(User createdBy, String name) {
		this.name = name;
		this.description = null;
		this.notes = null;
		this.servings = 0;
		this.prepTime = 0;
		this.cookTime = 0;
		this.createdBy = createdBy;
		this.version = 0;
		this.createdAt = OffsetDateTime.now();
		this.status = RecipeStatus.DRAFT;
		this.recipeDirections = null;
		this.recipeIngredients = null;
	}

	public Recipe(String name, String description, String notes, Integer servings, Integer prepTime, Integer cookTime,
			User createdBy, Integer version, OffsetDateTime createdAt, RecipeStatus status,
			Set<RecipeDirection> recipeDirections, Set<RecipeIngredient> recipeIngredients) {
		this.name = name;
		this.description = description;
		this.notes = notes;
		this.servings = servings;
		this.prepTime = prepTime;
		this.cookTime = cookTime;
		this.createdBy = createdBy;
		this.version = version;
		this.createdAt = createdAt;
		this.status = status;
		this.recipeDirections = recipeDirections;
		this.recipeIngredients = recipeIngredients;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
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

	public Integer getServings() {
		return servings;
	}

	public void setServings(Integer servings) {
		this.servings = servings;
	}

	public Integer getPrepTime() {
		return prepTime;
	}

	public void setPrepTime(Integer prepTime) {
		this.prepTime = prepTime;
	}

	public Integer getCookTime() {
		return cookTime;
	}

	public void setCookTime(Integer cookTime) {
		this.cookTime = cookTime;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public RecipeStatus getStatus() {
		return status;
	}

	public void setStatus(RecipeStatus status) {
		this.status = status;
	}

	public Integer getVersion() {
		return version;
	}

	public void setVersion(Integer version) {
		this.version = version;
	}

	public OffsetDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(OffsetDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public Set<RecipeDirection> getRecipeDirection() {
		return recipeDirections;
	}

	public void setRecipeDirection(Set<RecipeDirection> recipeDirections) {
		this.recipeDirections = recipeDirections;
	}

	public Recipe getParentRecipe() {
		return parentRecipe;
	}

	public void setParentRecipe(Recipe parentRecipe) {
		this.parentRecipe = parentRecipe;
	}

	public Set<RecipeIngredient> getRecipeIngredients() {
		return recipeIngredients;
	}

	public void setRecipeIngredients(Set<RecipeIngredient> recipeIngredients) {
		this.recipeIngredients = recipeIngredients;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(id);
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
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "Recipe [id=" + id + ", name=" + name + ", description=" + description + ", notes=" + notes
				+ ", servings=" + servings + ", prepTime=" + prepTime + ", cookTime=" + cookTime + ", version="
				+ version + ", createdAt=" + createdAt
				+ ", status=" + status + ", recipeDirections=" + recipeDirections + ", recipeIngredients="
				+ recipeIngredients + "]";
	}

}
