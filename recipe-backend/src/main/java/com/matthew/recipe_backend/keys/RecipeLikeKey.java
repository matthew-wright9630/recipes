package com.matthew.recipe_backend.keys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class RecipeLikeKey implements Serializable {
    @Column(name = "recipe_id")
    private Integer recipeId;

    @Column(name = "user_id")
    private Integer userId;

    public RecipeLikeKey() {
    }

    public RecipeLikeKey(Integer recipeId, Integer userId) {
        this.recipeId = recipeId;
        this.userId = userId;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((recipeId == null) ? 0 : recipeId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
        RecipeLikeKey other = (RecipeLikeKey) obj;
        if (recipeId == null) {
            if (other.recipeId != null)
                return false;
        } else if (!recipeId.equals(other.recipeId))
            return false;
        if (userId == null) {
            if (other.userId != null)
                return false;
        } else if (!userId.equals(other.userId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "RecipeLikeKey [recipeId=" + recipeId + ", userId=" + userId + "]";
    }

}