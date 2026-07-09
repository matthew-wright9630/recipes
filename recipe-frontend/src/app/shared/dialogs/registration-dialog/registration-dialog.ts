import { CommonModule } from '@angular/common';
import { Component, computed, DestroyRef, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import {
  MatDialog,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatError, MatFormField, MatLabel } from '@angular/material/form-field';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { AuthService } from '../../services/auth-service/auth.service';
import { AuthStateService } from '../../services/auth-state-service/auth-state.service';
import { debounceTime } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { User } from '../../models/user';

@Component({
  selector: 'app-registration-dialog',
  imports: [
    MatDialogModule,
    MatDialogContent,
    MatDialogTitle,
    CommonModule,
    MatError,
    MatIcon,
    MatLabel,
    MatFormField,
    ReactiveFormsModule,
    MatInput,
  ],
  templateUrl: './registration-dialog.html',
  styleUrl: './registration-dialog.scss',
})
export class RegistrationDialog {
  private dialog = inject(MatDialog);
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<RegistrationDialog>);
  private authService = inject(AuthService);
  private authStateService = inject(AuthStateService);
  private destroyRef = inject(DestroyRef);
  hidePassword = true;
  errorMessage = '';

  form = this.fb.group({
    username: ['', [Validators.required]],
    email: ['', [Validators.required, Validators.email]],
    password: ['', [Validators.required]],
    confirmPassword: ['', [Validators.required]],
  });

  ngOnInit(): void {
    // this.form.valueChanges
    //   .pipe(takeUntilDestroyed(this.destroyRef))
    //   .subscribe((values) => {
    //     this.formState.set({
    //       username: values.username || '',
    //       email: values.email || '',
    //       password: values.password || '',
    //       confirmPassword: values.confirmPassword || '',
    //     });
    //   });
    this.form.valueChanges
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => {
        this.updateValidators();
      });
  }

  // private formState = signal({
  //   username: '',
  //   email: '',
  //   password: '',
  //   confirmPassword: '',
  // });

  updateValidators(): void {
    if (this.form.value.password !== this.form.value.confirmPassword) {
      this.form.controls.confirmPassword.setErrors({ passwordMismatch: true });
    } else {
      this.form.controls.confirmPassword.setErrors(null);
    }
  }

  onSave(): void {
    this.form.markAllAsTouched();
    if (this.form.valid)
      if (
        this.form.value?.username &&
        this.form.value.email &&
        this.form.value.password
      )
        this.authService
          .register(
            this.form.value.username,
            this.form.value.email,
            this.form.value.password,
          )
          .subscribe({
            next: (response) => {
              const user: User = {
                id: response.id,
                email: response.email,
                username: response.username,
                userRole: response.role,
                avatarUrl: response.avatarUrl,
              };
              this.authStateService.login(response.accessToken, user);
              this.dialogRef.close(true);
            },
            error: (error) => {
              this.errorMessage = error.message;
            },
          });
  }
}
