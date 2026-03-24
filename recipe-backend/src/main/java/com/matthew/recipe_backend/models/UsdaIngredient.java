package com.matthew.recipe_backend.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.Set;

import jakarta.persistence.Column;
import jakarta.persistence.Table;

@Entity
@Table(name = "usda_ingredients")
public class UsdaIngredient {

	@Id
	@GeneratedValue
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

	@Column(name = "brand_name")
	private String brandName;

	@Column(name = "brand_owner")
	private String brandOwner;

	@OneToMany(mappedBy = "usdaIngredients")
	private Set<IngredientNutritionMap> ingredientMaps;

	public UsdaIngredient() {
	}

	public UsdaIngredient(Long fdcId, String name, double calories, double totalFat, double saturatedFat,
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

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Long getFdcId() {
		return fdcId;
	}

	public void setFdcId(Long fdcId) {
		this.fdcId = fdcId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getCalories() {
		return calories;
	}

	public void setCalories(double calories) {
		this.calories = calories;
	}

	public double getTotalFat() {
		return totalFat;
	}

	public void setTotalFat(double totalFat) {
		this.totalFat = totalFat;
	}

	public double getSaturatedFat() {
		return saturatedFat;
	}

	public void setSaturatedFat(double saturatedFat) {
		this.saturatedFat = saturatedFat;
	}

	public double getTransFat() {
		return transFat;
	}

	public void setTransFat(double transFat) {
		this.transFat = transFat;
	}

	public double getCholesterol() {
		return cholesterol;
	}

	public void setCholesterol(double cholesterol) {
		this.cholesterol = cholesterol;
	}

	public double getSodium() {
		return sodium;
	}

	public void setSodium(double sodium) {
		this.sodium = sodium;
	}

	public double getTotalCarbohydrates() {
		return totalCarbohydrates;
	}

	public void setTotalCarbohydrates(double totalCarbohydrates) {
		this.totalCarbohydrates = totalCarbohydrates;
	}

	public double getDietaryFiber() {
		return dietaryFiber;
	}

	public void setDietaryFiber(double dietaryFiber) {
		this.dietaryFiber = dietaryFiber;
	}

	public double getTotalSugars() {
		return totalSugars;
	}

	public void setTotalSugars(double totalSugars) {
		this.totalSugars = totalSugars;
	}

	public double getProtein() {
		return protein;
	}

	public void setProtein(double protein) {
		this.protein = protein;
	}

	public double getVitaminD() {
		return vitaminD;
	}

	public void setVitaminD(double vitaminD) {
		this.vitaminD = vitaminD;
	}

	public double getCalcium() {
		return calcium;
	}

	public void setCalcium(double calcium) {
		this.calcium = calcium;
	}

	public double getIron() {
		return iron;
	}

	public void setIron(double iron) {
		this.iron = iron;
	}

	public double getPotassium() {
		return potassium;
	}

	public void setPotassium(double potassium) {
		this.potassium = potassium;
	}

	public String getDatatype() {
		return datatype;
	}

	public void setDatatype(String datatype) {
		this.datatype = datatype;
	}

	public String getBrandName() {
		return brandName;
	}

	public void setBrandName(String brandName) {
		this.brandName = brandName;
	}

	public String getBrandOwner() {
		return brandOwner;
	}

	public void setBrandOwner(String brandOwner) {
		this.brandOwner = brandOwner;
	}

	public Set<IngredientNutritionMap> getIngredientMaps() {
		return ingredientMaps;
	}

	public void setIngredientMaps(Set<IngredientNutritionMap> ingredientMaps) {
		this.ingredientMaps = ingredientMaps;
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
		UsdaIngredient other = (UsdaIngredient) obj;
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
				+ ", datatype=" + datatype + ", brandName=" + brandName + ", brandOwner=" + brandOwner
				+ ", ingredientMaps=" + ingredientMaps + "]";
	}

}
