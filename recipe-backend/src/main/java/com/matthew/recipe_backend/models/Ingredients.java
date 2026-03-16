package com.matthew.recipe_backend.models;

import java.time.LocalDateTime;
import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

@Entity
public class Ingredients {
    
    @Id
    private String id;

    @Column(unique = true)
    private String name;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @OneToMany(mappedBy = "ingredient")
    private Set<IngredientNutritionMap> nutritionMaps;

    public Ingredients() {
    }

    public Ingredients(String name, LocalDateTime createdAt) {
        this.name = name;
        this.createdAt = createdAt;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
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
        Ingredients other = (Ingredients) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Ingredients [id=" + id + ", name=" + name + ", createdAt=" + createdAt + ", nutritionMaps="
                + nutritionMaps + "]";
    }
}
