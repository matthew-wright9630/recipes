import { Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { ImportRecipeService } from '../../services/import-recipe/import-recipe.service';
import { MatInputModule } from '@angular/material/input';
import { ImportRecipePreview } from '../import-recipe-preview/import-recipe-preview';
import { MatButtonModule } from '@angular/material/button';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-import-recipe-dialog',
  imports: [
    MatFormField,
    MatIcon,
    MatDialogContent,
    MatDialogActions,
    MatLabel,
    ReactiveFormsModule,
    MatInputModule,
    MatButtonModule,
  ],
  templateUrl: './import-recipe-dialog.html',
  styleUrl: './import-recipe-dialog.scss',
})
export class ImportRecipeDialog {
  private importRecipeService = inject(ImportRecipeService);
  private dialogRef = inject(MatDialogRef);
  private dialog = inject(MatDialog);
  private snackbar = inject(MatSnackBar);

  recipeUrl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.pattern(/^https?:\/\/.+/)],
  });

  onCancel(): void {
    this.dialogRef.close();
  }

  importRecipe(): void {
    if (this.recipeUrl.invalid) {
      return;
    }

    const url = this.recipeUrl.value;

    this.importRecipeService.fetchRecipePage(url).subscribe({
      next: (response) => {
        this.dialogRef.close();

        this.dialog.open(ImportRecipePreview, {
          width: '800px',
          maxWidth: '95vw',
          autoFocus: false,
          data: response,
        });
      },
      error: () => {
        this.snackbar.open(
          'We could not import this recipe. You can enter it manually instead.',
          '',
          { duration: 5000 },
        );
      },
    });
  }
}
