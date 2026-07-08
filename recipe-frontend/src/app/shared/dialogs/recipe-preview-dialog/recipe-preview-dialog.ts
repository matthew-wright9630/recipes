import { Component, computed, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle,
} from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthStateService } from '../../services//auth-state-service/auth-state.service';
import { RecipeEditDialog } from '../recipe-edit-dialog/recipe-edit-dialog';
import { ConfirmationDialog } from '../confirmation-dialog/confirmation-dialog';
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { RecipeStateService } from '../../services/recipe-state-service/recipe-state.service';
import { MatIcon } from '@angular/material/icon';
import { RecipeLikeService } from '../../services/recipe-like-service/recipe-like.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-recipe-preview-dialog',
  imports: [
    MatDialogModule,
    MatDialogContent,
    MatDialogTitle,
    RouterLink,
    CommonModule,
    MatIcon,
  ],
  templateUrl: './recipe-preview-dialog.html',
  styleUrl: './recipe-preview-dialog.scss',
})
export class RecipePreviewDialog {
  recipe = inject(MAT_DIALOG_DATA);
  recipeService = inject(RecipeService);
  recipeStateService = inject(RecipeStateService);
  recipeLikeService = inject(RecipeLikeService);
  authState = inject(AuthStateService);
  imageUrl: string = environment.imageBaseUrl + "recipes/";

  isOwner = computed(
    () => this.authState.currentUser()?.id === this.recipe.createdById,
  );

  sortedIngredients = computed(() =>
    [...this.recipe.recipeIngredients].sort(
      (a, b) => a.sortOrder - b.sortOrder,
    ),
  );

  sortedDirections = computed(() =>
    [...this.recipe.recipeDirections].sort((a, b) => a.sortOrder - b.sortOrder),
  );

  private dialog = inject(MatDialog);

  openEditDialog() {
    const raw = localStorage.getItem(`recipe-draft-${this.recipe.id}`);
    let data;

    if (raw) {
      const draft = JSON.parse(raw);
      const savedAt = new Date(draft.savedAt);
      const daysSince =
        (Date.now() - savedAt.getTime()) / (1000 * 60 * 60 * 24);

      if (daysSince <= 7) {
        data = { ...this.recipe, ...draft.values };
      } else {
        localStorage.removeItem(`recipe-draft-${this.recipe.id}`);
      }
    } else {
      data = this.recipe;
    }

    this.dialog.open(RecipeEditDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
      data: data,
    });
  }

  onRevise(): void {
    this.recipeService.reviseRecipe(this.recipe.id).subscribe({
      next: (result) => {
        if (result) {
          this.dialog.closeAll();
          this.recipeStateService.notifyRecipeUpdated(result);
          this.dialog.open(RecipeEditDialog, {
            width: '800px',
            maxWidth: '95vw',
            autoFocus: false,
            data: result,
          });
        }
      },
      error: (err) => console.error(err),
    });
  }

  onArchive(): void {
    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Archive ${this.recipe.name}`,
        message:
          "This will hide your recipe from search and other users' cookbooks. You can revise this recipe later to publish it.",
        confirmLabel: 'Archive',
        confirmColor: 'warn',
      },
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.recipeService.archiveRecipe(this.recipe.id).subscribe({
        next: (result) => {
          if (result) {
            this.recipeStateService.notifyRecipeUpdated(result);
          }
          this.dialog.closeAll();
        },
        error: (err) => console.error(err),
      });
    });
  }

  onDelete(): void {
    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Delete ${this.recipe.name}`,
        message:
          'This will permanently delete your draft. This action is not reversable.',
        confirmLabel: 'Delete',
        confirmColor: 'warn',
      },
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.recipeService.deleteDraftRecipe(this.recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeDeleted(this.recipe.id);
          this.dialog.closeAll();
        },
        error: (err) => console.error(err),
      });
    });
  }

  toggleFavorite(): void {
    if (!this.recipe.likedByCurrentUser) {
      this.recipeLikeService.likeRecipe(this.recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeUpdated(this.recipe.id);
          this.recipe.likedByCurrentUser = true;
        },
        error: (err) => console.error(err),
      });
    } else {
      this.recipeLikeService.unlikeRecipe(this.recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeUpdated(this.recipe.id);
          this.recipe.likedByCurrentUser = false;
        },
        error: (err) => console.error(err),
      });
    }
  }
}
