package com.matthew.recipe_backend.models;

import java.time.Instant;
import java.util.Objects;

import com.matthew.recipe_backend.enums.CookbookPermission;
import com.matthew.recipe_backend.keys.CookbookAccessKey;

import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "cookbook_access")
public class CookbookAccess {

	@EmbeddedId
	private CookbookAccessKey id;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("cookbookId")
	@JoinColumn(name = "cookbook_id")
	private Cookbook cookbook;

	@ManyToOne(fetch = FetchType.LAZY)
	@MapsId("userId")
	@JoinColumn(name = "user_id")
	private User user;

	@Enumerated(EnumType.STRING)
	private CookbookPermission permission;

	@Column(name = "granted_at")
	private Instant grantedAt;

	public CookbookAccess() {
	}

	public CookbookAccess(Cookbook cookbook, User user, CookbookPermission permission, Instant grantedAt) {
		this.cookbook = cookbook;
		this.user = user;
		this.permission = permission;
		this.grantedAt = grantedAt;
		if (cookbook != null && user != null) {
			this.id = new CookbookAccessKey(cookbook.getId(), user.getId());
		}
	}

	public CookbookAccessKey getId() {
		return id;
	}

	public void setId(CookbookAccessKey id) {
		this.id = id;
	}

	public Cookbook getCookbook() {
		return cookbook;
	}

	public void setCookbook(Cookbook cookbook) {
		this.cookbook = cookbook;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CookbookPermission getPermission() {
		return permission;
	}

	public void setPermission(CookbookPermission permission) {
		this.permission = permission;
	}

	public Instant getGrantedAt() {
		return grantedAt;
	}

	public void setGrantedAt(Instant grantedAt) {
		this.grantedAt = grantedAt;
	}

	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		CookbookAccess other = (CookbookAccess) obj;
		return Objects.equals(id, other.id);
	}

	@Override
	public String toString() {
		return "CookbookAccess [id=" + id + ", permission=" + permission + ", grantedAt=" + grantedAt + "]";
	}

}
