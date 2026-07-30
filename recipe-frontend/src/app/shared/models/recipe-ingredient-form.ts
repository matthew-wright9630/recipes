export interface RecipeIngredientForm {
  name: string;
  quantityWhole: number | null;
  quantityFraction: string | null;
  unit: string;
  notes: string | null;
}
