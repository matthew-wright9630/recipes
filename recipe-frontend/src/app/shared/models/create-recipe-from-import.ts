import { RecipeIngredient } from './recipe-ingredient';

export interface CreateRecipeFromImport {
  name: string;
  recipeIngredients: RecipeIngredient[];
  servings: number;
  prepTime: number;
  cookTime: number;
  originalUrl: string;
  imageUrl: string;
}
