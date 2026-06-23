import { Component, inject } from '@angular/core';
import {
  FormArray,
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogRef,
} from '@angular/material/dialog';
import { Recipe } from '../../models/recipe';
import { RecipeService } from '../../services/recipe.service';
import { RecipeStatus } from '../../models/recipe-status';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';

@Component({
  selector: 'app-recipe-edit-dialog',
  imports: [
    MatFormFieldModule,
    MatInputModule,
    MatButtonModule,
    MatDividerModule,
    MatSelectModule,
    MatDialogActions,
    ReactiveFormsModule,
  ],
  templateUrl: './recipe-edit-dialog.html',
  styleUrl: './recipe-edit-dialog.scss',
})
export class RecipeEditDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RecipeEditDialog>);
  private recipeService = inject(RecipeService);
  data = inject<Recipe>(MAT_DIALOG_DATA);

  form = this.fb.group({
    name: [this.data.name, [Validators.minLength(3), Validators.required]],
    description: [
      this.data.description,
      [Validators.minLength(2), Validators.required],
    ],
    notes: [this.data.notes],
    servings: [this.data.servings, [Validators.min(1), Validators.required]],
    prepTime: [this.data.prepTime, [Validators.min(0), Validators.required]],
    cookTime: [this.data.cookTime, [Validators.min(0), Validators.required]],
    status: [this.data.status, Validators.required],
    recipeIngredients: this.fb.array(
      [...this.data.recipeIngredients]
        .sort((a, b) => a.sortOrder - b.sortOrder)
        .map((ingredient) =>
          this.fb.group({
            name: [ingredient.name],
            quantity: [ingredient.quantity],
            unit: [ingredient.unit],
            notes: [ingredient.notes],
          }),
        ),
    ),
    recipeDirections: this.fb.array(
      [...this.data.recipeDirections]
        .sort((a, b) => a.stepNumber - b.stepNumber)
        .map((direction) =>
          this.fb.group({
            description: [direction.description],
          }),
        ),
    ),
  });

  onCancel(): void {
    sessionStorage.setItem('recipe-draft', JSON.stringify(this.form.value));
    this.dialogRef.close();
  }

  get ingredients() {
    return this.form.get('recipeIngredients') as FormArray;
  }

  get directions() {
    return this.form.get('recipeDirections') as FormArray;
  }

  statuses = Object.values(RecipeStatus).filter(
    (s) => s !== RecipeStatus.REMOVED,
  );

  addIngredient(): void {
    this.ingredients.push(
      this.fb.group({
        name: [''],
        quantity: [0],
        unit: [''],
        notes: [''],
      }),
    );
  }

  removeIngredient(index: number): void {
    this.ingredients.removeAt(index);
  }

  addDirection(): void {
    this.directions.push(
      this.fb.group({
        description: [''],
      }),
    );
  }

  removeDirection(index: number): void {
    this.directions.removeAt(index);
  }

  onSave(): void {
    const updated: Recipe = {
      ...this.data,
      ...this.form.getRawValue(),
    } as Recipe;

    console.log(this.data, this.data.recipeDirections);

    this.recipeService.updateRecipe(updated).subscribe({
      next: (result) => {
        sessionStorage.removeItem(`recipe-draft-${this.data.id}`);
        this.dialogRef.close(result);
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
