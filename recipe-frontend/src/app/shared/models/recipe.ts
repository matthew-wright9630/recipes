import { RecipeDirection } from "./recipe-direction";
import { RecipeIngredient } from "./recipe-ingredient";
import { RecipeStatus } from "./recipe-status";

export interface Recipe {
  name: string;
  description: string;
  notes: string;
  servings: number;
  prepTime: number;
  cookTime: number;
  version: number;
  status: RecipeStatus;
  recipeDirections: RecipeDirection[];
  recipeIngredients: RecipeIngredient[];
}
