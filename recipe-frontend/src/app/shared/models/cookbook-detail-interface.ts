import { Recipe } from './recipe';

export interface CookbookDetailInterface {
  id: number;
  name: string;
  imageUrl: string;
  updatedAt: string;
  description: string;
  recipes: Recipe[];
}
