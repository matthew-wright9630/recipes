import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { User } from '../../models/user';
import { environment } from '../../../../environments/environment';
import { SharedUser } from '../../models/shared-user';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  baseURL: string = environment.apiUrl + '/api/users';

  constructor(private http: HttpClient) {}

  getCurrentUser(): Observable<User> {
    return this.http.get<User>(this.baseURL + '/me');
  }

  getSharedUserDetails(email: string): Observable<SharedUser> {
    return this.http.get<SharedUser>(this.baseURL + '/shared', {
      params: {
        email: email,
      },
    });
  }
}
