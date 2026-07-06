import { HttpClient } from '@angular/common/http';
import { inject, Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserImageService {
  baseURL: string = 'http://localhost:8083/api/images';
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
