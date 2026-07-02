import { RecipeDirection } from './recipe-direction';
import { RecipeIngredient } from './recipe-ingredient';
import { RecipeStatus } from './recipe-status';

export interface Recipe {
  id: number;
  name: string;
  description: string;
  imageUrl: string;
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
  likeCount: number;
  likedByCurrentUser: boolean;
}
