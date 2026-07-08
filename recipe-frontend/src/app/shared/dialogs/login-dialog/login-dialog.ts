import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MatDialog,
  MatDialogContent,
  MatDialogModule,
  MatDialogRef,
  MatDialogTitle,
} from '@angular/material/dialog';
import { MatIcon } from '@angular/material/icon';
import {
  MatError,
  MatFormField,
  MatInput,
  MatLabel,
} from '@angular/material/input';
import { AuthService } from '../../services/auth-service/auth.service';
import { AuthStateService } from '../../services/auth-state-service/auth-state.service';
import { User } from '../../models/user';
import { RegistrationDialog } from '../registration-dialog/registration-dialog';

@Component({
  selector: 'app-login-dialog',
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
  templateUrl: './login-dialog.html',
  styleUrls: ['./login-dialog.scss'],
})
export class LoginDialogComponent {
  loginForm: FormGroup;
  hidePassword = true;
  errorMessage = '';

  private dialog = inject(MatDialog);
  private fb = inject(FormBuilder);
  private dialogRef = inject(MatDialogRef<LoginDialogComponent>);
  private authService = inject(AuthService);
  private authStateService = inject(AuthStateService);

  constructor() {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onLogin(): void {
    if (this.loginForm.invalid) return;

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe({
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
      error: () => {
        this.errorMessage = 'Invalid email or password';
      },
    });
  }

  onRegister(): void {
    this.dialogRef.close();
    this.dialog.open(RegistrationDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }
}
