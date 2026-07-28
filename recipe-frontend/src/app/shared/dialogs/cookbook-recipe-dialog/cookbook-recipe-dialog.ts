import { Component, inject } from '@angular/core';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { CookbookService } from '../../../features/cookbook/cookbook.service';
import { CookbookSelection } from '../../models/cookbook-selection';
import { MatListModule, MatSelectionListChange } from '@angular/material/list';
import { MatIcon } from '@angular/material/icon';
import { CreateCookbookRequest } from '../../models/create-cookbook-request';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';

@Component({
  selector: 'app-cookbook-recipe-dialog',
  imports: [
    MatIcon,
    MatListModule,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatButtonModule,
  ],
  templateUrl: './cookbook-recipe-dialog.html',
  styleUrl: './cookbook-recipe-dialog.scss',
})
export class CookbookRecipeDialog {
  recipe = inject(MAT_DIALOG_DATA);
  recipeService = inject(RecipeService);
  cookbookService = inject(CookbookService);

  listOfCookbooks: CookbookSelection[] = [];

  private dialog = inject(MatDialogRef);
  private snackbar = inject(MatSnackBar);

  creatingCookbook = false;
  newCookbookName = new FormControl('');

  ngOnInit() {
    this.cookbookService.getAllEditableCookbooks(this.recipe.id).subscribe({
      next: (data) => {
        this.listOfCookbooks = data;
      },
    });
  }

  onSelectionChange(event: MatSelectionListChange): void {
    event.options.forEach((option) => {
      option.value.containsRecipe = option.selected;
    });
  }

  createCookbook(): void {
    const name = this.newCookbookName.value?.trim();

    if (!name) {
      return;
    }

    const draftCookbook: CreateCookbookRequest = {
      name,
      description: '',
      imageUrl: 'food-PLACEHOLDER',
    };

    this.cookbookService.createCookbook(draftCookbook).subscribe((cookbook) => {
      if (cookbook) {
        this.listOfCookbooks.push({
          id: cookbook.id,
          name: cookbook.name,
          containsRecipe: true,
        });
      }

      this.creatingCookbook = false;
      this.newCookbookName.reset();
    });
  }

  save(): void {
    const request = {
      cookbookUpdates: this.listOfCookbooks.map((cookbook) => {
        return {
          cookbookId: cookbook.id,
          shouldContainRecipe: cookbook.containsRecipe,
        };
      }),
    };

    this.recipeService
      .updateRecipeCookbooks(this.recipe.id, request)
      .subscribe(() => {
        this.dialog.close();
      });
  }
}
