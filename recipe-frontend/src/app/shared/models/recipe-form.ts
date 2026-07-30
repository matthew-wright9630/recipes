import { RecipeDirection } from './recipe-direction';
import { RecipeIngredientForm } from './recipe-ingredient-form';
import { RecipeStatus } from './recipe-status';

export interface RecipeForm {
  name: string;
  imageUrl: string | null;
  description: string | null;
  notes: string | null;
  servings: number | null;
  prepTime: number | null;
  cookTime: number | null;
  status: RecipeStatus;
  recipeIngredients: RecipeIngredientForm[];
  recipeDirections: RecipeDirection[];
}
