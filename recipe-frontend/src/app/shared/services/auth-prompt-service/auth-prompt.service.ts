import { inject, Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { LoginDialogComponent } from '../../dialogs/login-dialog/login-dialog';
import { MatDialog } from '@angular/material/dialog';

@Injectable({
  providedIn: 'root',
})
export class AuthPromptService {
  private snackbar = inject(MatSnackBar);
  private dialog = inject(MatDialog);

  promptLogin(message: string, action?: () => void): void {
    const snackBarRef = this.snackbar.open(message, 'Login');

    snackBarRef.onAction().subscribe(() => {
      const dialogRef = this.dialog.open(LoginDialogComponent, {
        width: '800px',
        maxWidth: '95vw',
        autoFocus: false,
      });

      dialogRef.afterClosed().subscribe((loggedIn) => {
        if (loggedIn && action) {
          action();
        }
      });
    });
  }
}
