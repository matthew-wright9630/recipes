import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { User } from '../models/user';
import { Observable } from 'rxjs';
import { AuthResponse } from '../models/auth-response';

@Injectable({
  providedIn: 'root',
})
export class AuthServiceService {
  baseURL: string = 'http://localhost:8083/auth';

  constructor(private http: HttpClient) {}

  register(
    username: string,
    email: string,
    password: string,
  ): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.baseURL + '/register', {
      username,
      email,
      password,
    });
  }

  login(email: string, password: string): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(this.baseURL + '/login', {
      email,
      password,
    });
  }
}
