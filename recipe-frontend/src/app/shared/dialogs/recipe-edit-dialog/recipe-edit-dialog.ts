import { Component, DestroyRef, inject } from '@angular/core';
import {
  AbstractControl,
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
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { RecipeStatus } from '../../models/recipe-status';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButtonModule } from '@angular/material/button';
import { MatDividerModule } from '@angular/material/divider';
import { MatSelectModule } from '@angular/material/select';
import { debounceTime } from 'rxjs';
import { RecipeStateService } from '../../services/recipe-state-service/recipe-state.service';
import { MatIcon } from '@angular/material/icon';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

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
    MatIcon,
  ],
  templateUrl: './recipe-edit-dialog.html',
  styleUrl: './recipe-edit-dialog.scss',
})
export class RecipeEditDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RecipeEditDialog>);
  private recipeService = inject(RecipeService);
  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);
  data = inject<Recipe>(MAT_DIALOG_DATA);

  form = this.fb.group({
    name: [this.data.name, [Validators.minLength(3), Validators.required]],
    description: [
      this.data.description,
      [Validators.minLength(2), Validators.required],
    ],
    notes: [this.data.notes],
    servings: [this.data.servings],
    prepTime: [this.data.prepTime],
    cookTime: [this.data.cookTime],
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

  private readonly publishValidators = {
    servings: Validators.min(1),
    cookTime: Validators.min(0),
    prepTime: Validators.min(0),
  };

  ngOnInit(): void {
    this.updateValidators(this.data.status);

    this.form.get('status')?.valueChanges.subscribe((status) => {
      if (status) {
        this.updateValidators(status);
      }
    });

    this.form.valueChanges
      .pipe(debounceTime(10000), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        const draft = {
          savedAt: new Date().toISOString(),
          values: this.form.getRawValue(),
        };
        localStorage.setItem(
          `recipe-draft-${this.data.id}`,
          JSON.stringify(draft),
        );
      });
  }

  private updateValidators(status: RecipeStatus): void {
    const ingredientsArray = this.form.controls.recipeIngredients;
    const directionsArray = this.form.controls.recipeDirections;
    const servingsControl = this.form.get('servings');
    const cookTimeControl = this.form.get('cookTime');
    const prepTimeControl = this.form.get('prepTime');
    const nameControl = this.form.get('name');
    const descriptionControl = this.form.get('description');

    if (status === RecipeStatus.PUBLISHED) {
      nameControl?.addValidators(Validators.required);
      descriptionControl?.addValidators(Validators.required);
      servingsControl?.addValidators(this.publishValidators.servings);
      cookTimeControl?.addValidators(this.publishValidators.cookTime);
      prepTimeControl?.addValidators(this.publishValidators.prepTime);
      ingredientsArray.addValidators(minLengthArray(1));
      directionsArray.addValidators(minLengthArray(1));
      this.form.markAllAsTouched();
    } else {
      nameControl?.removeValidators(Validators.required);
      descriptionControl?.removeValidators(Validators.required);
      servingsControl?.removeValidators(this.publishValidators.servings);
      cookTimeControl?.removeValidators(this.publishValidators.cookTime);
      prepTimeControl?.removeValidators(this.publishValidators.prepTime);
      ingredientsArray.clearValidators();
      directionsArray.clearValidators();
    }

    nameControl?.updateValueAndValidity();
    descriptionControl?.updateValueAndValidity();
    servingsControl?.updateValueAndValidity();
    cookTimeControl?.updateValueAndValidity();
    prepTimeControl?.updateValueAndValidity();
    ingredientsArray.updateValueAndValidity();
    directionsArray.updateValueAndValidity();
    this.form.updateValueAndValidity();
  }

  onClose(): void {
    const draft = {
      savedAt: new Date().toISOString(),
      values: this.form.getRawValue(),
    };
    localStorage.setItem(`recipe-draft-${this.data.id}`, JSON.stringify(draft));
    this.dialogRef.close();
  }

  onCancel(): void {
    localStorage.removeItem(`recipe-draft-${this.data.id}`);
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

    this.recipeService.updateRecipe(updated).subscribe({
      next: (result) => {
        localStorage.removeItem(`recipe-draft-${this.data.id}`);
        this.recipeStateService.notifyRecipeUpdated(result.body as Recipe);
        this.dialogRef.close(result);
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}

// helper function
function minLengthArray(min: number) {
  return (control: AbstractControl) => {
    const array = control as FormArray;
    return array.length >= min ? null : { minLengthArray: true };
  };
}
