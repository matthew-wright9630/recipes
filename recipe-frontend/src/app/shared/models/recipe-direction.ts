import { RecipeIngredient } from "./recipe-ingredient";

export interface RecipeDirection {
  description: string;
  stepNumber: number;
  recipeIngredients: RecipeIngredient[];
}
