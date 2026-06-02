import { CanActivateFn } from '@angular/router';
import { AuthStateService } from './shared/services/auth-state.service';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { filter, map, take } from 'rxjs';
import { toObservable } from '@angular/core/rxjs-interop';

export const authGuard: CanActivateFn = (route) => {
  const authService: AuthStateService = inject(AuthStateService);
  const router: Router = inject(Router);

  const state = authService.authState();

  return toObservable(authService.authState).pipe(
    filter((state) => state !== 'loading'),
    take(1),
    map((state) =>
      state === 'authenticated' ? true : router.createUrlTree(['/']),
    ),
  );
};
