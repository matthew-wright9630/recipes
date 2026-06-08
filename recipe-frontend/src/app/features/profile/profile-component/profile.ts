import { CommonModule } from '@angular/common';
import { Component, effect, inject, signal } from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeService } from '../../recipe/recipe.service';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { AuthStateService } from '../../../shared/services/auth-state.service';

@Component({
  selector: 'app-profile',
  imports: [CommonModule, RecipeComponent],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  recipeList = signal<Recipe[]>([]);

  private recipeService = inject(RecipeService);
  authState = inject(AuthStateService);

  constructor() {
    effect(() => {
      this.recipeService.getRecipesByUser().subscribe((recipes) => {
        if (recipes) {
          this.recipeList.set(recipes);
        }
      });
    });
  }
}
