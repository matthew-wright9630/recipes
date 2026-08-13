import {
  HttpClient,
  HttpContext,
  HttpParams,
  HttpResponse,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Recipe } from '../../models/recipe';
import { map, Observable } from 'rxjs';
import { Page } from '../../models/page';
import { environment } from '../../../../environments/environment';
import { response } from 'express';
import { UpdateRecipeCookbookRequest } from '../../models/update-recipe-cookbook-request';
import { SKIP_GLOBAL_ERROR } from '../../http-context-tokens';

@Injectable({
  providedIn: 'root',
})
export class RecipeService {
  baseURL: string = environment.apiUrl + '/api/recipes';

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

  getRecipeViewHistoryPreview() {
    return this.http
      .get<
        Recipe[]
      >(this.baseURL + '/me/history/preview', { observe: 'response' })
      .pipe(map((res) => res.body));
  }

  getRecipeViewHistory(page: number = 0, size: number = 12) {
    return this.http.get<Page<Recipe>>(
      this.baseURL + `/me/history?page=${page}&size=${size}`,
    );
  }

  getLikedRecipePreview() {
    return this.http
      .get<
        Recipe[]
      >(this.baseURL + '/me/liked/preview', { observe: 'response' })
      .pipe(map((res) => res.body));
  }

  getLikedRecipes(page: number = 0, size: number = 12) {
    return this.http.get<Page<Recipe>>(
      this.baseURL + `/me/liked?page=${page}&size=${size}`,
    );
  }

  bookmarkRecipe(id: number): Observable<HttpResponse<void>> {
    return this.http.post<void>(`${this.baseURL}/${id}/bookmark`, null, {
      withCredentials: true,
      observe: 'response',
      context: new HttpContext().set(SKIP_GLOBAL_ERROR, true),
    });
  }

  unBookmarkRecipe(id: number): Observable<HttpResponse<void>> {
    return this.http.delete<void>(`${this.baseURL}/${id}/bookmark`, {
      withCredentials: true,
      observe: 'response',
      context: new HttpContext().set(SKIP_GLOBAL_ERROR, true),
    });
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

  downloadRecipePdf(id: number): Observable<Blob> {
    return this.http.get(this.baseURL + '/' + id + '/pdf', {
      responseType: 'blob',
    });
  }

  updateRecipeCookbooks(
    recipeId: number,
    cookbookUpdates: UpdateRecipeCookbookRequest,
  ): Observable<void> {
    return this.http.put<void>(
      `${this.baseURL}/${recipeId}/cookbooks`,
      cookbookUpdates,
    );
  }
}
