import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Recipe } from '../../models/recipe';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  baseURL: string = 'http://localhost:8083/api/recipes';

  constructor(private http: HttpClient) {}

  getAllRecipes(): Observable<Recipe[]> {
    return this.http
      .get<Recipe[]>(this.baseURL, { observe: 'response' })
      .pipe(map((res) => res.body || []));
  }

  getRecipeById(id: number): Observable<Recipe | null> {
    return this.http
      .get<Recipe>(this.baseURL + '/' + id, {
        withCredentials: true,
        observe: 'response',
      })
      .pipe(map((res) => res.body));
  }

  getRecipesByUser() {
    return this.http
      .get<Recipe[]>(this.baseURL + '/me', { observe: 'response' })
      .pipe(map((res) => res.body || []));
  }

  getRecipeViewHistoryByUser(limit: number) {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http
      .get<Recipe[]>(this.baseURL + '/history', { observe: 'response', params })
      .pipe(map((res) => res.body));
  }

  createDraftRecipe(recipe: Recipe) {
    return this.http.post<Recipe>(this.baseURL, recipe, {
      observe: 'response',
    });
  }

  updateRecipe(recipe: Recipe) {
    return this.http.put<Recipe>(this.baseURL + '/' + recipe.id, recipe, {
      observe: 'response',
    });
  }
}
