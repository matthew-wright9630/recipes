import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Cookbook } from '../../shared/models/cookbook';
import { Page } from '../../shared/models/page';
import { CookbookRequest } from '../../shared/models/cookbook-request';
import { CookbookSelection } from '../../shared/models/cookbook-selection';
import { CookbookDetailInterface } from '../../shared/models/cookbook-detail-interface';

@Injectable({
  providedIn: 'root',
})
export class CookbookService {
  baseURL: string = environment.apiUrl + '/api/cookbooks';

  http = inject(HttpClient);

  getAllAccessibleCookbooks(
    page: number = 0,
    size: number = 12,
    searchTerm: string = '',
  ): Observable<Page<Cookbook>> {
    return this.http.get<Page<Cookbook>>(
      this.baseURL +
        `/accessible?page=${page}&size=${size}&search=${searchTerm}`,
    );
  }

  getAllEditableCookbooks(recipeId: number): Observable<CookbookSelection[]> {
    return this.http.get<CookbookSelection[]>(this.baseURL + '/list', {
      params: {
        recipeId: recipeId.toString(),
      },
    });
  }

  createCookbook(cookbook: CookbookRequest) {
    return this.http
      .post<Cookbook>(this.baseURL, cookbook, { observe: 'response' })
      .pipe(map((res) => res.body));
  }

  editCookbook(cookbook: CookbookRequest, id: number) {
    return this.http
      .put<Cookbook>(this.baseURL + `/${id}`, cookbook, { observe: 'response' })
      .pipe(map((res) => res.body));
  }

  getCookbookById(id: number): Observable<CookbookDetailInterface> {
    return this.http.get<CookbookDetailInterface>(this.baseURL + `/${id}`, {});
  }

  getAllRecipesInCookbook(recipeId: number): Observable<CookbookSelection[]> {
    return this.http.get<CookbookSelection[]>(this.baseURL + `/list`, {});
  }

  deleteDraftRecipe(id: number) {
    return this.http.delete<Cookbook>(this.baseURL + '/' + id, {
      observe: 'response',
    });
  }
}
