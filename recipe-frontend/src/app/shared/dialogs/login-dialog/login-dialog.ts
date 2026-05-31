import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import {
  FormBuilder,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import {
  MatDialogActions,
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
import { RouterLink } from '@angular/router';
import { AuthServiceService } from '../../services/auth-service.service';

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

  constructor(
    private fb: FormBuilder,
    private dialogRef: MatDialogRef<LoginDialogComponent>,
    private authService: AuthServiceService,
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', Validators.required],
    });
  }

  onLogin(): void {
    if (this.loginForm.invalid) return;

    const { email, password } = this.loginForm.value;

    this.authService.login(email, password).subscribe((response) => {
      console.log(response);
    });
  }

  onRegister(): void {
    this.dialogRef.close('register');
  }
}
