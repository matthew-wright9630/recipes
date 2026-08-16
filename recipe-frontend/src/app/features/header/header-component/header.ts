import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { HeaderService } from '../header.service';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { LoginDialogComponent } from '../../../shared/dialogs/login-dialog/login-dialog';
import { AuthStateService } from '../../../shared/services/auth-state-service/auth-state.service';
import { RegistrationDialog } from '../../../shared/dialogs/registration-dialog/registration-dialog';
import { AuthPromptService } from '../../../shared/services/auth-prompt-service/auth-prompt.service';

@Component({
  selector: 'app-header-component',
  imports: [
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatMenuModule,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {
  private authPrompt = inject(AuthPromptService);
  private headerService = inject(HeaderService);
  private dialog = inject(MatDialog);
  private router = inject(Router);

  authState = inject(AuthStateService);

  openLogin() {
    this.dialog.open(LoginDialogComponent, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  openRegistration(): void {
    this.dialog.open(RegistrationDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  onMyRecipesClick(): void {
    console.log('Clicked');
    if (this.authState.currentUser()) {
      this.router.navigate(['/recipes']);
    } else {
      this.authPrompt.promptLogin('Login to view your recipes.', () => {
        this.router.navigate(['/recipes']);
      });
    }
  }

  onCookbooksClick(): void {
    if (this.authState.currentUser()) {
      this.router.navigate(['/cookbooks']);
    } else {
      this.authPrompt.promptLogin('Login to view your cookbooks.', () => {
        this.router.navigate(['/cookbooks']);
      });
    }
  }

  profileClick() {
    this.router.navigate(['/profile']);
  }
}
