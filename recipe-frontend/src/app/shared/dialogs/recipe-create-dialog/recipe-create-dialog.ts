import { Component, inject } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { Recipe } from '../../models/recipe';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { RecipeStateService } from '../../services/recipe-state-service/recipe-state.service';
import { RecipeEditDialog } from '../recipe-edit-dialog/recipe-edit-dialog';

@Component({
  selector: 'app-recipe-create-dialog',
  imports: [
    MatDialogContent,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInputModule,
    MatDialogActions,
  ],
  templateUrl: './recipe-create-dialog.html',
  styleUrl: './recipe-create-dialog.scss',
})
export class RecipeCreateDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RecipeCreateDialog>);
  private recipeService = inject(RecipeService);
  private recipeStateService = inject(RecipeStateService);
  private dialog = inject(MatDialog);

  form = this.fb.group({
    name: ['', [Validators.minLength(3), Validators.required]],
    description: ['', [Validators.minLength(2), Validators.required]],
  });

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    const draftRecipe: Recipe = {
      ...this.form.getRawValue(),
    } as Recipe;

    this.recipeService.createDraftRecipe(draftRecipe).subscribe({
      next: (result) => {
        this.dialogRef.close(result);
        this.dialog.open(RecipeEditDialog, {
          width: '800px',
          maxWidth: '95vw',
          autoFocus: false,
          data: this.form.getRawValue(),
        });
        this.recipeStateService.notifyRecipeUpdated(result.body as Recipe);
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
