import { RecipeDirection } from './recipe-direction';
import { RecipeIngredient } from './recipe-ingredient';
import { RecipeStatus } from './recipe-status';
import { User } from './user';

export interface Recipe {
  id: number;
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
  createdAt: string;
  createdById: number;
}
