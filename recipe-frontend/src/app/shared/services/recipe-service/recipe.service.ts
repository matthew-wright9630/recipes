import { HttpClient, HttpParams, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Recipe } from '../../models/recipe';
import { map, Observable } from 'rxjs';
import { Page } from '../../models/page';

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

  getPublishedRecipes(
    page: number = 0,
    size: number = 12,
    searchTerm: string = '',
  ): Observable<Page<Recipe>> {
    return this.http.get<Page<Recipe>>(
      this.baseURL + `/publish?page=${page}&size=${size}&search=${searchTerm}`,
    );
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

  updateDraftRecipe(recipe: Recipe) {
    return this.http.put<Recipe>(
      this.baseURL + '/' + recipe.id + '/draft',
      recipe,
      {
        observe: 'response',
      },
    );
  }

  updateAndPublishRecipe(recipe: Recipe) {
    return this.http.put<Recipe>(
      this.baseURL + '/' + recipe.id + '/publish',
      recipe,
      {
        observe: 'response',
      },
    );
  }

  reviseRecipe(id: number) {
    return this.http.post<Recipe>(this.baseURL + '/' + id + '/revise', {
      observe: 'response',
    });
  }

  archiveRecipe(id: number) {
    return this.http.put<Recipe>(this.baseURL + '/' + id + '/archive', {
      observe: 'response',
    });
  }

  deleteDraftRecipe(id: number) {
    return this.http.delete<Recipe>(this.baseURL + '/' + id, {
      observe: 'response',
    });
  }
}
