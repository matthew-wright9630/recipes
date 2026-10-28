import { RecipeIngredient } from './recipe-ingredient';
import { RecipeNote } from './recipe-note';

export interface ImportRecipeResponse {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
  notes: string;
  servings: number;
  prepTime: number;
  cookTime: number;
  recipeIngredients: RecipeIngredient[];
  recipeNotes: RecipeNote[];
}
