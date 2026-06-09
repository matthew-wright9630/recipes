import {
  computed,
  inject,
  Injectable,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { User } from '../models/user';
import { isPlatformBrowser } from '@angular/common';
import { AuthService } from './auth.service';
import { UserService } from './user.service';
import { catchError, map, Observable, of, tap, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  private _currentUser = signal<User | null>(null);
  private _accessToken = signal<string | null>(null);
  private platformId = inject(PLATFORM_ID);
  private userService = inject(UserService);
  private http = inject(HttpClient);
  private router = inject(Router);

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  private _initialized = signal(false);
  initialized = this._initialized.asReadonly();

  currentUser = this._currentUser.asReadonly();
  isLoggedIn = computed(() => {
    return this._initialized() && this._currentUser() !== null;
  });
  accessToken = this._accessToken.asReadonly();

  authState = computed(() => {
    if (!this._initialized()) return 'loading';
    return this._currentUser() ? 'authenticated' : 'unauthenticated';
  });

  login(token: string, user: User): void {
    this._accessToken.set(token);
    this._currentUser.set(user);
    if (this.isBrowser()) localStorage.setItem('accessToken', token);
  }

  logout(): void {
    this._currentUser.set(null);
    this._accessToken.set(null);
    if (this.isBrowser()) {
      localStorage.removeItem('accessToken');
    }
    this.router.navigate(['/']);
  }

  initialize(): Observable<void> {
    if (!this.isBrowser()) {
      return of(void 0);
    }
    const token = localStorage.getItem('accessToken');

    if (!token) {
      return of(void 0);
    }

    this._accessToken.set(token);

    return this.userService.getCurrentUser().pipe(
      tap((user) => this._currentUser.set(user)),
      map(() => void 0),
      catchError((error) => {
        if (error.status === 401) {
          this.logout();
          return of(void 0);
        }

        return throwError(() => error);
      }),
    );
  }
}
