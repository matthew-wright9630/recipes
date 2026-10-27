import { Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatDialogActions, MatDialogContent } from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { ImportRecipeService } from '../../services/import-recipe/import-recipe.service';
import { MatInputModule } from '@angular/material/input';

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
  ],
  templateUrl: './import-recipe-dialog.html',
  styleUrl: './import-recipe-dialog.scss',
})
export class ImportRecipeDialog {
  private importRecipeService = inject(ImportRecipeService);

  recipeUrl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required, Validators.pattern(/^https?:\/\/.+/)],
  });

  importRecipe(): void {
    if (this.recipeUrl.invalid) {
      return;
    }

    const url = this.recipeUrl.value;

    this.importRecipeService.fetchRecipePage(this.recipeUrl.value).subscribe({
      next: (html) => {
        console.log(html);
      },
      error: (error) => {
        console.error('Failed to fetch recipe page:', error);
      },
    });
  }
}
