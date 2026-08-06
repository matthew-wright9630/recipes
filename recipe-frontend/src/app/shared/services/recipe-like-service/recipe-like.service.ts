import { HttpClient, HttpContext, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';
import { SKIP_GLOBAL_ERROR } from '../../http-context-tokens';

@Injectable({
  providedIn: 'root',
})
export class RecipeLikeService {
  baseURL: string = environment.apiUrl + '/api/recipe-likes';

  constructor(private http: HttpClient) {}

  likeRecipe(id: number): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this.baseURL}/${id}/like`, null, {
      withCredentials: true,
      observe: 'response',
      context: new HttpContext().set(SKIP_GLOBAL_ERROR, true),
    });
  }

  unlikeRecipe(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.baseURL}/${id}/like`, {
      withCredentials: true,
      observe: 'response',
      context: new HttpContext().set(SKIP_GLOBAL_ERROR, true),
    });
  }
}
