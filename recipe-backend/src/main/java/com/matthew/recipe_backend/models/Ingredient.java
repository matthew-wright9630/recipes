package com.matthew.recipe_backend.models;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredients")
public class Ingredient {
    
    @Id
    private long id;

    @Column(unique = true)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ingredient")
    private Set<IngredientNutritionMap> nutritionMaps;

    public Ingredient() {
    }

    public Ingredient(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<IngredientNutritionMap> getNutritionMaps() {
        return nutritionMaps;
    }

    public void setNutritionMaps(Set<IngredientNutritionMap> nutritionMaps) {
        this.nutritionMaps = nutritionMaps;
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
        Ingredient other = (Ingredient) obj;
        if (id == 0) {
            if (other.id != 0)
                return false;
        } else if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Ingredients [id=" + id + ", name=" + name + ", createdAt=" + createdAt + ", nutritionMaps="
                + nutritionMaps + "]";
    }
}
