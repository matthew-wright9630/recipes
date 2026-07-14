import {
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
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
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { UserImageService } from '../../services/user-image-service/user-image.service';
import { environment } from '../../../../environments/environment';
import { DEFAULT_RECIPE_IMAGES } from '../../constants/default-images';

@Component({
  selector: 'app-recipe-create-dialog',
  imports: [
    MatDialogContent,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatInputModule,
    MatDialogActions,
    MatButton,
    MatIcon,
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
  private imageService = inject(UserImageService);

  userImages: string[] = [];
  defaultImages = DEFAULT_RECIPE_IMAGES;
  imageUrl: string = environment.imageBaseUrl + 'recipes/';

  form = this.fb.group({
    name: ['', [Validators.minLength(3), Validators.required]],
    description: [''],
    imageUrl: ['food-PLACEHOLDER', [Validators.required]],
  });

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
    const track = container.querySelector('.recipe-create__image-track');
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

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    const draftRecipe: Recipe = {
      ...this.form.getRawValue(),
    } as Recipe;

    this.recipeService.createDraftRecipe(draftRecipe).subscribe({
      next: (result) => {
        console.log(result);
        this.dialogRef.close(result);
        this.dialog.open(RecipeEditDialog, {
          width: '800px',
          maxWidth: '95vw',
          autoFocus: false,
          data: result.body,
        });
        this.recipeStateService.notifyRecipeUpdated(result.body as Recipe);
      },
      error: (err) => {
        console.error(err);
      },
    });
  }
}
