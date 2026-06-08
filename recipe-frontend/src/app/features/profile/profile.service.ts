import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  baseURL: string = 'http://localhost:8083/api/users';

  constructor(private http: HttpClient) {}
}
