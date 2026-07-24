import { inject, Injectable } from '@angular/core';
import { environment } from '../../../environments/environment';
import { HttpClient } from '@angular/common/http';
import { map, Observable } from 'rxjs';
import { Cookbook } from '../../shared/models/cookbook';

@Injectable({
  providedIn: 'root',
})
export class CookbookService {
  baseURL: string = environment.apiUrl + '/api/cookbooks';

  http = inject(HttpClient);

  getAllAccessibleCookbooks(): Observable<Cookbook[]> {
    return this.http
      .get<Cookbook[]>(this.baseURL, { observe: 'response' })
      .pipe(map((res) => res.body || []));
  }
}
