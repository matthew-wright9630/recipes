import { HttpClient, HttpResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Recipe } from '../../shared/models/recipe';
import { map, Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RecipeService {
  baseURL: string = 'http://localhost:8083/api/recipes'

  constructor(private http: HttpClient) {}

  getAllRecipes(): Observable<Recipe[]> {
    return this.http.get<Recipe[]>(this.baseURL, { observe: 'response' })
      .pipe(map(res => res.body || []));
  }

  getRecipeById(id: number): Observable<Recipe | null> {
    console.log(this.baseURL + "/" + id);
    return this.http.get<Recipe>(this.baseURL + "/" + id, {observe: 'response'})
      .pipe(map(res => res.body));
  }
}
