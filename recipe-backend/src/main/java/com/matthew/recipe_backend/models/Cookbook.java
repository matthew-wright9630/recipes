package com.matthew.recipe_backend.models;

import java.time.Instant;
import java.time.OffsetDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "cookbooks")
public class Cookbook {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;

	@Column
	private String name;

	@Column
	private String description;

	@Column
	private boolean deleted;

	@Column(name = "created_at")
	private Instant createdAt;

	@Column(name = "updated_at", nullable = false)
	private OffsetDateTime updatedAt;

	@Column(name = "image_url")
	private String imageUrl;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", referencedColumnName = "id")
	private User ownerId;

	public Cookbook() {
	}

	public Cookbook(String name, String description, boolean deleted, Instant createdAt, OffsetDateTime updatedAt,
			String imageUrl, User ownerId) {
		this.name = name;
		this.description = description;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.updatedAt = updatedAt;
		this.imageUrl = imageUrl;
		this.ownerId = ownerId;
	}

	public Cookbook(String name, String description, String imageUrl, User ownerId) {
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.deleted = false;
		this.createdAt = Instant.now();
		this.updatedAt = OffsetDateTime.now();
		this.ownerId = ownerId;
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

	public boolean isDeleted() {
		return deleted;
	}

	public void setDeleted(boolean deleted) {
		this.deleted = deleted;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public OffsetDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(OffsetDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public User getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(User ownerId) {
		this.ownerId = ownerId;
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
		Cookbook other = (Cookbook) obj;
		if (id != other.id)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Cookbook [id=" + id + ", name=" + name + ", description" + description + ", deleted=" + deleted
				+ ", createdAt=" + createdAt + ", updatedAt=" + updatedAt
				+ ", imageUrl" + imageUrl + "]";
	}

}