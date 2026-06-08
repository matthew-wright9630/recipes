import { CanActivateFn } from '@angular/router';
import { AuthStateService } from './shared/services/auth-state.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { distinctUntilChanged, filter, map, take } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

export const authGuard: CanActivateFn = (route) => {
  const authService: AuthStateService = inject(AuthStateService);
  const router: Router = inject(Router);

  return toObservable(authService.currentUser).pipe(
    take(1),
    map((user) => (user ? true : router.createUrlTree(['/']))),
  );
};
