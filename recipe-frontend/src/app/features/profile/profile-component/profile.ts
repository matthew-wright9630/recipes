import { CommonModule } from '@angular/common';
import { Component, effect, inject, signal } from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeService } from '../../recipe/recipe.service';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { AuthStateService } from '../../../shared/services/auth-state.service';
import { MatCard, MatCardTitle } from '@angular/material/card';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    RecipeComponent,
    MatCard,
    MatCardTitle,
    MatDivider,
    MatButton,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  recipeList = signal<Recipe[]>([]);

  recipeHistory = signal<Recipe[]>([]);

  private recipeService = inject(RecipeService);
  authState = inject(AuthStateService);

  constructor() {
    effect(() => {
      this.recipeService.getRecipesByUser().subscribe((recipes) => {
        if (recipes) {
          this.recipeList.set(recipes);
        }
      });
      this.recipeService.getRecipeViewHistoryByUser(3).subscribe((recipes) => {
        if (recipes) {
          this.recipeHistory.set(recipes);
        }
      });
    });
  }

  logout() {
    this.authState.logout();
  }
}
