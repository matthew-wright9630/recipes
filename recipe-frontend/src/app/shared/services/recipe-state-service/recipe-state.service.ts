import { Injectable } from '@angular/core';
import { ReplaySubject } from 'rxjs';
import { Recipe } from '../../models/recipe';

@Injectable({
  providedIn: 'root',
})
export class RecipeStateService {
  private recipeUpdated = new ReplaySubject<Recipe>(1);
  recipeUpdated$ = this.recipeUpdated.asObservable();

  notifyRecipeUpdated(recipe: Recipe): void {
    this.recipeUpdated.next(recipe);
  }
}
