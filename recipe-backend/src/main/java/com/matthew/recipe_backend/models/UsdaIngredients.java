package com.matthew.recipe_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "usda_ingredients")
public class UsdaIngredients {

    @Id
    @Column(name = "id")
    private long id;

    @Column(name = "fdc_id", unique = true)
    private Long fdcId;

    @Column(name = "name")
    private String name;

    @Column(name = "calories")
    private double calories;

    @Column(name = "total_fat")
    private double totalFat;

    @Column(name = "saturated_fat")
    private double saturatedFat;

    @Column(name = "trans_fat")
    private double transFat;

    @Column(name = "cholesterol")
    private double cholesterol;

    @Column(name = "sodium")
    private double sodium;

    @Column(name = "total_carbohydrates")
    private double totalCarbohydrates;

    @Column(name = "dietary_fiber")
    private double dietaryFiber;

    @Column(name = "total_sugars")
    private double totalSugars;

    @Column(name = "protein")
    private double protein;

    @Column(name = "vitamin_d")
    private double vitaminD;

    @Column(name = "calcium")
    private double calcium;

    @Column(name = "iron")
    private double iron;

    @Column(name = "potassium")
    private double potassium;

    @Column(name = "datatype")
    private String datatype;

    @Column(name = "brandName")
    private String brandName;

    @Column(name = "brandOwner")
    private String brandOwner;

    public UsdaIngredients() {
    }

    public UsdaIngredients(Long fdcId, String name, double calories, double totalFat, double saturatedFat,
            double transFat, double cholesterol, double sodium, double totalCarbohydrates, double dietaryFiber,
            double totalSugars, double protein, double vitaminD, double calcium, double iron, double potassium,
            String datatype, String brandName, String brandOwner) {
        this.fdcId = fdcId;
        this.name = name;
        this.calories = calories;
        this.totalFat = totalFat;
        this.saturatedFat = saturatedFat;
        this.transFat = transFat;
        this.cholesterol = cholesterol;
        this.sodium = sodium;
        this.totalCarbohydrates = totalCarbohydrates;
        this.dietaryFiber = dietaryFiber;
        this.totalSugars = totalSugars;
        this.protein = protein;
        this.vitaminD = vitaminD;
        this.calcium = calcium;
        this.iron = iron;
        this.potassium = potassium;
        this.datatype = datatype;
        this.brandName = brandName;
        this.brandOwner = brandOwner;
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
        UsdaIngredients other = (UsdaIngredients) obj;
        if (id != other.id)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "UsdaIngredients [id=" + id + ", fdcId=" + fdcId + ", name=" + name + ", calories=" + calories
                + ", totalFat=" + totalFat + ", saturatedFat=" + saturatedFat + ", transFat=" + transFat
                + ", cholesterol=" + cholesterol + ", sodium=" + sodium + ", totalCarbohydrates=" + totalCarbohydrates
                + ", dietaryFiber=" + dietaryFiber + ", totalSugars=" + totalSugars + ", protein=" + protein
                + ", vitaminD=" + vitaminD + ", calcium=" + calcium + ", iron=" + iron + ", potassium=" + potassium
                + ", datatype=" + datatype + ", brandName=" + brandName + ", brandOwner=" + brandOwner + "]";
    }

    
}
