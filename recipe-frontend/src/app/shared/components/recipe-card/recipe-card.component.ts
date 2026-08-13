import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Recipe } from '../../models/recipe';
import { MatDialog } from '@angular/material/dialog';
import { RecipePreviewDialog } from '../../dialogs/recipe-preview-dialog/recipe-preview-dialog';
import { AuthStateService } from '../../services/auth-state-service/auth-state.service';
import { RecipeStateService } from '../../services/recipe-state-service/recipe-state.service';
import { RecipeLikeService } from '../../services/recipe-like-service/recipe-like.service';
import { MatIcon } from '@angular/material/icon';
import { environment } from '../../../../environments/environment';
import { MatSnackBar } from '@angular/material/snack-bar';
import { AuthPromptService } from '../../services/auth-prompt-service/auth-prompt.service';
import { RecipeService } from '../../services/recipe-service/recipe.service';

@Component({
  selector: 'app-recipe',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIcon],
  templateUrl: './recipe-card.component.html',
  styleUrl: './recipe-card.component.scss',
})
export class RecipeComponent {
  @Input() recipe!: Recipe;
  private dialog = inject(MatDialog);
  private snackbar = inject(MatSnackBar);
  authState = inject(AuthStateService);
  recipeStateService = inject(RecipeStateService);
  recipeLikeService = inject(RecipeLikeService);
  recipeService = inject(RecipeService);
  authPrompt = inject(AuthPromptService);
  backendUrl: string = environment.apiUrl + '/uploads/';
  imageUrl: string = environment.imageBaseUrl + 'recipes/';

  constructor() {}
  openRecipe(recipe: Recipe) {
    this.dialog.open(RecipePreviewDialog, {
      data: recipe,
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  toggleFavorite(recipe: Recipe): void {
    if (this.authState.currentUser()) {
      if (!this.recipe.likedByCurrentUser) {
        this.recipeLikeService.likeRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.likedByCurrentUser = true;
            this.recipeStateService.notifyRecipeUpdated(recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not save your like. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      } else {
        this.recipeLikeService.unlikeRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.likedByCurrentUser = false;
            this.recipeStateService.notifyRecipeUpdated(recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not remove your like. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      }
    } else {
      this.authPrompt.promptLogin('Login to like this recipe!', () => {
        this.recipeService.getRecipeById(this.recipe.id).subscribe({
          next: (updatedRecipe) => {
            if (updatedRecipe) {
              this.recipe.likedByCurrentUser = updatedRecipe.likedByCurrentUser;
              this.recipe.bookmarkedByCurrentUser =
                updatedRecipe.bookmarkedByCurrentUser;
              if (!updatedRecipe.likedByCurrentUser) {
                this.toggleFavorite(this.recipe);
              }
            }
          },
        });
      });
    }
  }

  toggleBookmark(recipe: Recipe): void {
    if (this.authState.currentUser()) {
      if (!this.recipe.bookmarkedByCurrentUser) {
        this.recipeService.bookmarkRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.bookmarkedByCurrentUser = true;
            this.recipeStateService.notifyRecipeUpdated(recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not save your bookmark. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      } else {
        this.recipeService.unBookmarkRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.bookmarkedByCurrentUser = false;
            this.recipeStateService.notifyRecipeUpdated(recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not remove your bookmark. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      }
    } else {
      this.authPrompt.promptLogin('Login to bookmark this recipe!', () => {
        this.recipeService.getRecipeById(this.recipe.id).subscribe({
          next: (updatedRecipe) => {
            if (updatedRecipe) {
              this.recipe.likedByCurrentUser = updatedRecipe.likedByCurrentUser;
              this.recipe.bookmarkedByCurrentUser =
                updatedRecipe.bookmarkedByCurrentUser;
              if (!updatedRecipe.bookmarkedByCurrentUser) {
                this.toggleFavorite(this.recipe);
              }
            }
          },
        });
      });
    }
  }
}
