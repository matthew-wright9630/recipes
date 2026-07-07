import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class ProfileService {
  baseURL: string = environment.apiUrl + '/api/users';

  constructor(private http: HttpClient) {}
}
