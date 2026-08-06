import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';
import { catchError, throwError } from 'rxjs';
import { SKIP_GLOBAL_ERROR } from './http-context-tokens';

export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const snackbar = inject(MatSnackBar);

  return next(req).pipe(
    catchError((err) => {
      if (!req.context.get(SKIP_GLOBAL_ERROR)) {
        snackbar.open(
          'An unexpected error occurred. Please check your connection and try again.',
          'Dismiss',
          { duration: 5000 },
        );
      }

      return throwError(() => {
        return err;
      });
    }),
  );
};
