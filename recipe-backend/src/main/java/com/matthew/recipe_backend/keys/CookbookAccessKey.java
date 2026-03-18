package com.matthew.recipe_backend.keys;

import java.io.Serializable;
import java.util.Objects;

import jakarta.persistence.Embeddable;

@Embeddable
public class CookbookAccessKey implements Serializable {

	private Long cookbookId;
	private Long userId;

	public CookbookAccessKey() {}

	public CookbookAccessKey(Long cookbookId, Long userId) {
		this.cookbookId = cookbookId;
		this.userId = userId;
	}

	public Long getCookbookId() {
		return cookbookId;
	}

	public void setCookbookId(Long cookbookId) {
		this.cookbookId = cookbookId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cookbookId, userId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CookbookAccessKey other = (CookbookAccessKey) obj;
		return Objects.equals(cookbookId, other.cookbookId) && Objects.equals(userId, other.userId);
	}

	@Override
	public String toString() {
		return "CookbookAccessKey [cookbookId=" + cookbookId + ", userId=" + userId + "]";
	}
}
