import { Component, inject } from '@angular/core';
import { FormBuilder, Validators } from '@angular/forms';
import { MatDialogRef } from '@angular/material/dialog';
import { RecipeService } from '../../services/recipe.service';
import { Recipe } from '../../models/recipe';

@Component({
  selector: 'app-recipe-create-dialog',
  imports: [],
  templateUrl: './recipe-create-dialog.html',
  styleUrl: './recipe-create-dialog.scss',
})
export class RecipeCreateDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RecipeCreateDialog>);
  private recipeService = inject(RecipeService);

  form = this.fb.group({
    name: ['', [Validators.minLength(3), Validators.required]],
    description: ['', [Validators.minLength(2), Validators.required]],
  });

  onCancel(): void {
    sessionStorage.setItem('recipe-draft', JSON.stringify(this.form.value));
    this.dialogRef.close();
  }

  onSave(): void {
    const updated: Recipe = {
      ...this.form.getRawValue(),
    } as Recipe;

    this.recipeService.updateRecipe(updated).subscribe({
      next: (result) => {
        sessionStorage.removeItem(`recipe-draft-${this.form.value}`);
        this.dialogRef.close(result);
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
