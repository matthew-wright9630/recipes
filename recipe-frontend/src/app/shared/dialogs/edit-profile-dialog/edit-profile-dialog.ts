import { Component, inject } from '@angular/core';
import { FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import {
  MAT_DIALOG_DATA,
  MatDialogModule,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { UserService } from '../../services/user-service/user.service';
import { MatSnackBar } from '@angular/material/snack-bar';
import { UserImageService } from '../../services/user-image-service/user-image.service';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-edit-profile-dialog',
  imports: [
    ReactiveFormsModule,
    MatDialogModule,
    MatButtonModule,
    MatIconModule,
    MatFormFieldModule,
    MatInputModule,
  ],
  templateUrl: './edit-profile-dialog.html',
  styleUrl: './edit-profile-dialog.scss',
})
export class EditProfileDialog {
  private dialogRef = inject(MatDialogRef<EditProfileDialog>);
  private userService = inject(UserService);
  private imageService = inject(UserImageService);
  private snackbar = inject(MatSnackBar);
  data = inject(MAT_DIALOG_DATA);

  imageUrl: string = environment.imageBaseUrl + 'avatars/';

  avatarUrl: string = this.data.avatarUrl;

  usernameControl = new FormControl('', {
    nonNullable: true,
    validators: [Validators.required],
  });

  ngOnInit(): void {
    this.usernameControl.setValue(this.data.username);

    if (this.data.avatarUrl) {
      // this.avatarPreview = this.data.avatarUrl;
    }
  }

  onClose(): void {
    this.dialogRef.close();
  }

  onAvatarSelected(event: Event): void {
    const input = event.target as HTMLInputElement;

    if (!input.files?.length) {
      return;
    }

    const file = input.files[0];

    if (!file.type.startsWith('image/')) {
      return;
    }

    const formData = new FormData();
    formData.append('file', file);

    this.imageService.uploadAvatar(formData).subscribe({
      next: (baseKey) => {
        this.avatarUrl = baseKey;
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

  save(): void {
    if (this.usernameControl.invalid) {
      this.usernameControl.markAsTouched();
      return;
    }

    this.userService
      .updateProfile(this.usernameControl.value, this.avatarUrl)
      .subscribe({
        next: () => {
          this.dialogRef.close({
            username: this.usernameControl.value,
            avatarUrl: this.avatarUrl,
          });
        },
      });
  }
}
