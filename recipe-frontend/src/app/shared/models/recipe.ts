import { RecipeDirection } from './recipe-direction';
import { RecipeIngredient } from './recipe-ingredient';
import { RecipeNote } from './recipe-note';
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
  recipeNotes: RecipeNote[];
  createdAt: string;
  createdById: number;
  likeCount: number;
  savedCount: number;
  likedByCurrentUser: boolean;
  bookmarkedByCurrentUser: boolean;
}
