import { CommonModule } from '@angular/common';
import { Component, effect, signal } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { RecipeService } from '../recipe.service';
import { Recipe } from '../../../shared/models/recipe';
import { toSignal } from '@angular/core/rxjs-interop';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';

@Component({
  selector: 'app-recipe-list',
  imports: [CommonModule, MatCardModule, RecipeComponent, MatGridListModule],
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.scss'
})
export class RecipeListComponent {

  recipeList = signal<Recipe[]>([]);

  constructor(
    private recipeService: RecipeService,
  ) {
    effect(() => {
      this.recipeService.getAllRecipes().subscribe((recipes) => {
        this.recipeList.set(recipes.map((r,i) => ({...r, _uid: '${r.name}-${i}-${Date.now()}'})));
      })
    })
  }
}
