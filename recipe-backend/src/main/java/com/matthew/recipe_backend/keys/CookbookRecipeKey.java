package com.matthew.recipe_backend.keys;

import java.io.Serializable;
import jakarta.persistence.Embeddable;

@Embeddable
public class CookbookRecipeKey implements Serializable {

    private Long cookbookId;
    private Long recipeId;

    public CookbookRecipeKey() {}

    public CookbookRecipeKey(Long cookbookId, Long recipeId) {
        this.cookbookId = cookbookId;
        this.recipeId = recipeId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((cookbookId == null) ? 0 : cookbookId.hashCode());
        result = prime * result + ((recipeId == null) ? 0 : recipeId.hashCode());
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
        CookbookRecipeKey other = (CookbookRecipeKey) obj;
        if (cookbookId == null) {
            if (other.cookbookId != null)
                return false;
        } else if (!cookbookId.equals(other.cookbookId))
            return false;
        if (recipeId == null) {
            if (other.recipeId != null)
                return false;
        } else if (!recipeId.equals(other.recipeId))
            return false;
        return true;
    }

    
}
