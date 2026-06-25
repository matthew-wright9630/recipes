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
}
