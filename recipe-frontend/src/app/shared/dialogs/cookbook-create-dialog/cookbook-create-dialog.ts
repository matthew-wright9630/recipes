import {
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButton } from '@angular/material/button';
import {
  MatDialog,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { CookbookService } from '../../services/cookbook-service/cookbook.service';
import { UserImageService } from '../../services/user-image-service/user-image.service';
import { DEFAULT_RECIPE_IMAGES } from '../../constants/default-images';
import { environment } from '../../../../environments/environment';
import { Cookbook } from '../../models/cookbook';
import { CookbookRequest } from '../../models/cookbook-request';
import { CookbookStateService } from '../../services/cookbook-state/cookbook-state.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { Router } from '@angular/router';

@Component({
  selector: 'app-cookbook-create-dialog',
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
  templateUrl: './cookbook-create-dialog.html',
  styleUrl: './cookbook-create-dialog.scss',
})
export class CookbookCreateDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<CookbookCreateDialog>);
  private cookbookService = inject(CookbookService);
  private dialog = inject(MatDialog);
  private imageService = inject(UserImageService);
  private cookbookStateService = inject(CookbookStateService);
  private snackbar = inject(MatSnackBar);
  private router = inject(Router);

  errorMessage = '';
  userImages: string[] = [];
  defaultImages = DEFAULT_RECIPE_IMAGES;
  imageUrl: string = environment.imageBaseUrl + 'recipes/';

  form = this.fb.group({
    name: ['', [Validators.minLength(3), Validators.required]],
    description: [''],
    imageUrl: ['food-PLACEHOLDER', [Validators.required]],
  });

  ngOnInit() {
    this.getListOfImages();
  }

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
    const track = container.querySelector('.cookbook-create__image-track');
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
      error: () => {
        this.snackbar.open(
          'Image upload failed. Please try again.',
          'Dismiss',
          { duration: 5000 },
        );
      },
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

    const draftCookbook: CookbookRequest = {
      ...this.form.getRawValue(),
    } as CookbookRequest;

    this.cookbookService.createCookbook(draftCookbook).subscribe({
      next: (result) => {
        this.dialogRef.close(result);
        this.cookbookStateService.notifyCookbookUpdated(result as Cookbook);
      },
      error: (error) => {
        this.errorMessage = error.error.message;
      },
    });
  }
}
