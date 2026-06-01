import {
  computed,
  inject,
  Injectable,
  PLATFORM_ID,
  signal,
} from '@angular/core';
import { User } from '../models/user';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root',
})
export class AuthStateService {
  private _currentUser = signal<User | null>(null);
  private _accessToken = signal<string | null>(null);
  private platformId = inject(PLATFORM_ID);

  private isBrowser(): boolean {
    return isPlatformBrowser(this.platformId);
  }

  currentUser = this._currentUser.asReadonly();
  isLoggedIn = computed(() => this._currentUser() !== null);
  accessToken = this._accessToken.asReadonly();

  login(token: string, user: User): void {
    console.log(user);
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
  }

  restoreSession(): void {
    if (!this.isBrowser()) return;
    const token = localStorage.getItem('accessToken');
    if (token) {
      this._accessToken.set(token);
    }
  }
}
