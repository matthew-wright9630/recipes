import { CommonModule } from '@angular/common';
import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { AuthStateService } from '../../../shared/services/auth-state-service/auth-state.service';
import { MatCard, MatCardTitle } from '@angular/material/card';
import { MatDivider } from '@angular/material/divider';
import { MatButton } from '@angular/material/button';
import { RecipeService } from '../../../shared/services/recipe-service/recipe.service';
import { RecipeStateService } from '../../../shared/services/recipe-state-service/recipe-state.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

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

  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.recipeStateService.recipeUpdated$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((updatedRecipe) => {
        if (updatedRecipe) {
          this.recipeService.getRecipesByUser().subscribe((recipes) => {
            if (recipes) {
              this.recipeList.set(recipes);
            }
          });
          this.recipeService
            .getRecipeViewHistoryByUser(3)
            .subscribe((recipes) => {
              if (recipes) {
                this.recipeHistory.set(recipes);
              }
            });
        }
      });

    this.recipeStateService.recipeDeleted$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((deletedId) => {
        this.recipeList.update((recipes) =>
          recipes.filter((r) => r.id !== deletedId),
        );
      });
  }

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
