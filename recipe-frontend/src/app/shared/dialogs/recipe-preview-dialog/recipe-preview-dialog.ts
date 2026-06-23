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
import { AuthStateService } from '../../services/auth-state.service';
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
    const draft = sessionStorage.getItem(`recipe-draft-${this.recipe.id}`);
    const data = draft ? { ...this.recipe, ...JSON.parse(draft) } : this.recipe;

    this.dialog.open(RecipeEditDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
      data: data,
    });
  }
}
