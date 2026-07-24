package com.matthew.recipe_backend.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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

	@Column(name = "image_url")
	private String imageUrl;

	public Cookbook() {
	}

	public Cookbook(String name, String description, boolean deleted, Instant createdAt, String imageUrl) {
		this.name = name;
		this.description = description;
		this.deleted = deleted;
		this.createdAt = createdAt;
		this.imageUrl = imageUrl;
	}

	public Cookbook(String name, String description, String imageUrl) {
		this.name = name;
		this.description = description;
		this.imageUrl = imageUrl;
		this.deleted = false;
		this.createdAt = Instant.now();
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

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
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
				+ ", createdAt=" + createdAt
				+ ", imageUrl" + imageUrl + "]";
	}

}