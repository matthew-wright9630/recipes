import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RecipeLikeService {
  baseURL: string = 'http://localhost:8083/api/recipe-likes';

  constructor(private http: HttpClient) {}

  likeRecipe(id: number): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this.baseURL}/${id}/like`, null, {
      withCredentials: true,
      observe: 'response',
    });
  }

  unlikeRecipe(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.baseURL}/${id}/like`, {
      withCredentials: true,
      observe: 'response',
    });
  }
}
