package com.matthew.recipe_backend.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;

@Entity
@Table(name = "recipes")
public class Recipes {

    @Id
    @Column(name = "id")
    private long id;

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

    @Column(name = "created_by")
    private Integer createdBy;

    @Column(nullable = false)
    private Boolean deleted;

    @Column(nullable = false)
    private Integer version;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    public Recipes() {
    }

    public Recipes(String name, String description, String notes, Integer servings, Integer prepTime,
            Integer cookTime, Integer createdBy, Boolean deleted, Integer version, OffsetDateTime createdAt) {
        this.name = name;
        this.description = description;
        this.notes = notes;
        this.servings = servings;
        this.prepTime = prepTime;
        this.cookTime = cookTime;
        this.createdBy = createdBy;
        this.deleted = deleted;
        this.version = version;
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

    public Integer getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(Integer createdBy) {
        this.createdBy = createdBy;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
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
        Recipes other = (Recipes) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Recipes [id=" + id + ", name=" + name + ", description=" + description + ", notes=" + notes
                + ", servings=" + servings + ", prepTime=" + prepTime + ", cookTime=" + cookTime + ", createdBy="
                + createdBy + ", deleted=" + deleted + ", version=" + version + ", createdAt=" + createdAt + "]";
    }
}
