import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

@Injectable({
  providedIn: 'root',
})
export class UserImageService {
  baseURL: string = environment.apiUrl + '/api/images';
  private http = inject(HttpClient);

  getImages(): Observable<string[]> {
    return this.http.get<string[]>(this.baseURL + '/my-images');
  }

  uploadImage(formData: FormData): Observable<string> {
    return this.http.post(this.baseURL + '/upload', formData, {
      responseType: 'text',
    });
  }
}
