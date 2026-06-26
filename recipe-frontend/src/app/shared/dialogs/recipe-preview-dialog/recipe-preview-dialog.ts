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

@Component({
  selector: 'app-recipe-preview-dialog',
  imports: [
    MatDialogModule,
    MatDialogContent,
    MatDialogActions,
    MatDialogTitle,
    RouterLink,
    CommonModule,
  ],
  templateUrl: './recipe-preview-dialog.html',
  styleUrl: './recipe-preview-dialog.scss',
})
export class RecipePreviewDialog {
  recipe = inject(MAT_DIALOG_DATA);
  recipeService = inject(RecipeService);
  recipeStateService = inject(RecipeStateService);
  isOwner = computed(
    () => this.authState.currentUser()?.id === this.recipe.createdById,
  );

  sortedIngredients = computed(() =>
    [...this.recipe.recipeIngredients].sort(
      (a, b) => a.sortOrder - b.sortOrder,
    ),
  );

  private dialog = inject(MatDialog);
  private authState = inject(AuthStateService);

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

  onArchive(): void {
    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Archive ${this.recipe.name}`,
        message:
          "This will hide your recipe from search and other users' cookbooks. You can republish it later.",
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
        next: (result) => {
          this.recipeStateService.notifyRecipeDeleted(this.recipe.id);
          this.dialog.closeAll();
        },
        error: (err) => console.error(err),
      });
    });
  }
}
