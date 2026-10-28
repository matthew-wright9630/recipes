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
import { ImportRecipeResponse } from '../../models/import-recipe-response';

@Injectable({
  providedIn: 'root',
})
export class ImportRecipeService {
  baseURL: string = environment.apiUrl + '/api/recipe-importer';

  constructor(private http: HttpClient) {}

  fetchRecipePage(url: string): Observable<ImportRecipeResponse> {
    const params = new HttpParams().set('url', url);

    return this.http.post<ImportRecipeResponse>(this.baseURL, null, { params });
  }
}
