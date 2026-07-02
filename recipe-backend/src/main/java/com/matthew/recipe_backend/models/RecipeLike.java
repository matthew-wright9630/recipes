package com.matthew.recipe_backend.models;

import java.time.OffsetDateTime;

import com.matthew.recipe_backend.keys.RecipeLikeKey;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_like")
public class RecipeLike {

    @EmbeddedId
    private RecipeLikeKey id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("userId")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recipeId")
    private Recipe recipe;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public RecipeLike() {
    }

    public RecipeLike(User user, Recipe recipe, OffsetDateTime createdAt) {
        this.user = user;
        this.recipe = recipe;
        this.createdAt = createdAt;
    }

    public RecipeLikeKey getId() {
        return id;
    }

    public void setId(RecipeLikeKey id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Recipe getRecipe() {
        return recipe;
    }

    public void setRecipe(Recipe recipe) {
        this.recipe = recipe;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
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
        RecipeLike other = (RecipeLike) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RecipeLike [id=" + id + ", user=" + user + ", recipe=" + recipe + ", createdAt=" + createdAt + "]";
    }

}
