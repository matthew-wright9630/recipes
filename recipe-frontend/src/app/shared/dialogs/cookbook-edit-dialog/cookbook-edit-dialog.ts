import {
  Component,
  ElementRef,
  inject,
  signal,
  ViewChild,
} from '@angular/core';
import { CookbookRequest } from '../../models/cookbook-request';
import { Cookbook } from '../../models/cookbook';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';
import { CookbookService } from '../../services/cookbook-service/cookbook.service';
import { UserImageService } from '../../services/user-image-service/user-image.service';
import { CookbookStateService } from '../../services/cookbook-state/cookbook-state.service';
import { DEFAULT_RECIPE_IMAGES } from '../../constants/default-images';
import { environment } from '../../../../environments/environment';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInputModule } from '@angular/material/input';
import { MatButton } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { CookbookDetailInterface } from '../../models/cookbook-detail-interface';
import { MatSnackBar } from '@angular/material/snack-bar';

@Component({
  selector: 'app-cookbook-edit-dialog',
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
  templateUrl: './cookbook-edit-dialog.html',
  styleUrl: './cookbook-edit-dialog.scss',
})
export class CookbookEditDialog {
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<CookbookEditDialog>);
  private cookbookService = inject(CookbookService);
  private dialog = inject(MatDialog);
  private imageService = inject(UserImageService);
  private cookbookStateService = inject(CookbookStateService);
  private snackbar = inject(MatSnackBar);

  errorMessage: string = '';
  data = inject<CookbookDetailInterface>(MAT_DIALOG_DATA);
  userImages: string[] = [];
  defaultImages = DEFAULT_RECIPE_IMAGES;
  imageUrl: string = environment.imageBaseUrl + 'recipes/';

  form = this.fb.group({
    name: [this.data.name, [Validators.minLength(3), Validators.required]],
    description: [this.data.description],
    imageUrl: [this.data.imageUrl, [Validators.required]],
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
    const track = container.querySelector('.cookbook-edit__image-track');
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

    const editedCookbook: CookbookRequest = {
      ...this.form.getRawValue(),
    } as CookbookRequest;

    this.cookbookService.editCookbook(editedCookbook, this.data.id).subscribe({
      next: (result) => {
        this.dialogRef.close(result);
        this.cookbookStateService.notifyCookbookUpdated(result as Cookbook);
      },
      error: (err) => {
        this.errorMessage = err.error.message;
      },
    });
  }
}
