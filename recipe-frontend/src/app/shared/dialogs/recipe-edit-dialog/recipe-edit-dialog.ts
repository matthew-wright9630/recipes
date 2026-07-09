import {
  Component,
  computed,
  DestroyRef,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import {
  AbstractControl,
  FormArray,
  FormBuilder,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
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
import { ConfirmationDialog } from '../confirmation-dialog/confirmation-dialog';
import { DEFAULT_RECIPE_IMAGES } from '../../constants/default-images';
import { UserImageService } from '../../services/user-image-service/user-image.service';
import { environment } from '../../../../environments/environment';

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
  private dialog = inject(MatDialog);
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RecipeEditDialog>);
  private recipeService = inject(RecipeService);
  private recipeStateService = inject(RecipeStateService);
  private imageService = inject(UserImageService);
  private destroyRef = inject(DestroyRef);

  private imageBaseUrl: string = 'uploads';

  data = inject<Recipe>(MAT_DIALOG_DATA);
  readonly RecipeStatus = RecipeStatus;
  defaultImages = DEFAULT_RECIPE_IMAGES;
  userImages: string[] = [];
  backendUrl: string = environment.apiUrl + '/uploads/';
  imageUrl: string = environment.imageBaseUrl + 'recipes/';

  form = this.fb.group({
    name: [this.data.name, [Validators.minLength(3), Validators.required]],
    imageUrl: [this.data.imageUrl],
    description: [this.data.description],
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
    this.updateValidators(RecipeStatus.PUBLISHED);

    this.form.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((values) => {
        this.formState.set({
          name: values.name || '',
          description: values.description || '',
          servings: values.servings || 0,
          cookTime: values.cookTime || 0,
          prepTime: values.prepTime || 0,
          ingredientCount: this.ingredients.length,
          directionCount: this.directions.length,
        });
      });

    this.form.valueChanges
      .pipe(debounceTime(1000), takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        const draft = {
          savedAt: new Date().toISOString(),
          values: this.form.getRawValue(),
        };
        localStorage.setItem(
          `recipe-draft-${this.data.id}`,
          JSON.stringify(draft),
        );

        this.updateValidators(RecipeStatus.PUBLISHED);
      });

    this.getListOfImages();
  }

  private updateValidators(status: RecipeStatus): void {
    const ingredientsArray = this.form.controls.recipeIngredients;
    const directionsArray = this.form.controls.recipeDirections;
    const servingsControl = this.form.get('servings');
    const cookTimeControl = this.form.get('cookTime');
    const prepTimeControl = this.form.get('prepTime');
    const nameControl = this.form.get('name');

    if (status === RecipeStatus.PUBLISHED) {
      nameControl?.addValidators(Validators.required);
      servingsControl?.addValidators(this.publishValidators.servings);
      cookTimeControl?.addValidators(this.publishValidators.cookTime);
      prepTimeControl?.addValidators(this.publishValidators.prepTime);
      ingredientsArray.addValidators(minLengthArray(1));
      directionsArray.addValidators(minLengthArray(1));
      this.form.markAllAsTouched();
    } else {
      nameControl?.removeValidators(Validators.required);
      servingsControl?.removeValidators(this.publishValidators.servings);
      cookTimeControl?.removeValidators(this.publishValidators.cookTime);
      prepTimeControl?.removeValidators(this.publishValidators.prepTime);
      ingredientsArray.clearValidators();
      directionsArray.clearValidators();
    }

    nameControl?.updateValueAndValidity();
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

  selectImage(image: string): void {
    this.form.get('imageUrl')?.setValue(image);
  }

  @ViewChild('imageTrack') imageTrack!: ElementRef;
  currentOffset = 0;
  readonly SCROLL_AMOUNT = 133; // image width + gap
  @ViewChild('imageTrack') imageTrackContainer!: ElementRef;
  maxOffset = signal(100);

  scrollImages(direction: number): void {
    const container = this.imageTrackContainer.nativeElement;
    const track = container.querySelector('.recipe-edit__image-track');
    this.maxOffset.set(track.scrollWidth - container.clientWidth);

    this.currentOffset = Math.max(
      0,
      Math.min(
        this.currentOffset + direction * this.SCROLL_AMOUNT,
        this.maxOffset(),
      ),
    );
    track.style.transform = `translateX(-${this.currentOffset}px)`;
  }

  selectedImageFile: File | null = null;

  onFileSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files || input.files.length === 0) {
      return;
    }

    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      return;
    }

    this.selectedImageFile = file;

    this.form.patchValue({
      imageUrl: null,
    });

    this.selectedImageFile = file;
    this.uploadImage();
  }

  uploadImage(): void {
    if (!this.selectedImageFile) return;

    const formData = new FormData();
    formData.append('file', this.selectedImageFile);

    this.imageService.uploadImage(formData).subscribe({
      next: (baseKey) => {
        this.form.get('imageUrl')?.setValue(baseKey);
        this.selectedImageFile = null;
        this.getListOfImages();
      },
      error: (err) => console.error(err),
    });
  }

  getImageUrl(baseKey: string, size: 'thumb' | 'medium'): string {
    return `${environment.imageBaseUrl}/${baseKey}-${size}.jpg`;
  }

  getListOfImages(): void {
    this.imageService.getImages().subscribe({
      next: (images) => {
        this.userImages = images;
      },
      error: (err) => console.error(err),
    });
  }

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

  private formState = signal({
    name: this.data.name,
    description: this.data.description,
    servings: this.data.servings,
    cookTime: this.data.cookTime,
    prepTime: this.data.prepTime,
    ingredientCount: this.data.recipeIngredients.length,
    directionCount: this.data.recipeDirections.length,
  });

  canPublish = computed(
    () =>
      !!this.formState().name?.trim() &&
      !!this.formState().description?.trim() &&
      this.formState().servings > 0 &&
      this.formState().cookTime >= 0 &&
      this.formState().prepTime >= 0 &&
      this.formState().ingredientCount >= 1 &&
      this.formState().directionCount >= 1,
  );

  onSave(): void {
    const updated: Recipe = {
      ...this.data,
      ...this.form.getRawValue(),
    } as Recipe;

    this.recipeService.updateDraftRecipe(updated).subscribe({
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

  onPublish(): void {
    this.updateValidators(RecipeStatus.PUBLISHED);

    if (this.form.invalid) return;

    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Publish ${this.data.name}`,
        message:
          'This will make your recipe visible to everyone. Are you sure?',
        confirmLabel: 'Publish',
        confirmColor: 'primary',
      },
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;

      const updated = {
        ...this.data,
        ...this.form.getRawValue(),
        status: RecipeStatus.PUBLISHED,
      } as Recipe;

      this.recipeService.updateAndPublishRecipe(updated).subscribe({
        next: (result) => {
          localStorage.removeItem(`recipe-draft-${this.data.id}`);
          if (result.body) {
            this.recipeStateService.notifyRecipeUpdated(result.body);
          }
          this.dialogRef.close(result);
        },
        error: (err) => console.error(err),
      });
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
