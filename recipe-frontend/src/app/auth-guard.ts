import { CanActivateFn } from '@angular/router';
import { AuthStateService } from './shared/services/auth-state.service';
import { inject } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from './shared/dialogs/login-dialog/login-dialog';
import { map } from 'rxjs';
import { Router } from '@angular/router';

export const authGuard: CanActivateFn = (route, state) => {
  const authService: AuthStateService = inject(AuthStateService);
  const dialog = inject(MatDialog);
  const router: Router = inject(Router);

  if (authService.isLoggedIn()) {
    return true;
  }

  dialog.open(LoginDialogComponent);

  return router.createUrlTree(['/homepage'], {
    queryParams: { loginRequired: 'true' },
  });
};
