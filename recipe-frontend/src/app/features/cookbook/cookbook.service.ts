import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Cookbook } from '../../shared/models/cookbook';
import { Page } from '../../shared/models/page';

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
}
