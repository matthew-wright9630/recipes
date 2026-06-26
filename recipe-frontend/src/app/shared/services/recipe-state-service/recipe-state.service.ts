import { Injectable } from '@angular/core';
import { ReplaySubject, Subject } from 'rxjs';
import { Recipe } from '../../models/recipe';

@Injectable({
  providedIn: 'root',
})
export class RecipeStateService {
  private recipeUpdated = new ReplaySubject<Recipe>(1);
  recipeUpdated$ = this.recipeUpdated.asObservable();

  private recipeDeleted = new Subject<number>();
  recipeDeleted$ = this.recipeDeleted.asObservable();

  notifyRecipeUpdated(recipe: Recipe): void {
    this.recipeUpdated.next(recipe);
  }

  notifyRecipeDeleted(id: number): void {
    this.recipeDeleted.next(id);
  }
}
