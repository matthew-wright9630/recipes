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
import { RouterLink } from '@angular/router';
import { CdkOverlayOrigin } from '@angular/cdk/overlay';

@Component({
  selector: 'app-profile',
  imports: [
    CommonModule,
    RecipeComponent,
    MatCard,
    MatCardTitle,
    MatDivider,
    MatButton,
    RouterLink,
    CdkOverlayOrigin,
  ],
  templateUrl: './profile.html',
  styleUrl: './profile.scss',
})
export class Profile {
  likedRecipeList = signal<Recipe[]>([]);

  recipeHistory = signal<Recipe[]>([]);

  private recipeService = inject(RecipeService);
  authState = inject(AuthStateService);
  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.recipeService.getLikedRecipePreview().subscribe((recipes) => {
      if (recipes) this.likedRecipeList.set(recipes);
    });

    this.recipeService.getRecipeViewHistoryPreview().subscribe((recipes) => {
      if (recipes) this.recipeHistory.set(recipes);
    });

    this.recipeStateService.recipeUpdated$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.recipeService.getLikedRecipePreview().subscribe((recipes) => {
          if (recipes) this.likedRecipeList.set(recipes);
        });
      });

    this.recipeStateService.recipeDeleted$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((deletedId) => {
        this.likedRecipeList.update((recipes) =>
          recipes.filter((r) => r.id !== deletedId),
        );
      });
  }

  logout() {
    this.authState.logout();
  }
}
