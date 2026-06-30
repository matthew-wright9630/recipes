import { CommonModule } from '@angular/common';
import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { RecipeService } from '../../../shared/services/recipe-service/recipe.service';
import { RecipeStateService } from '../../../shared/services/recipe-state-service/recipe-state.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { Page } from '../../../shared/models/page';
import { MatIcon } from '@angular/material/icon';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatInput } from '@angular/material/input';
import { debounceTime, distinctUntilChanged } from 'rxjs';

@Component({
  selector: 'app-recipe-list',
  imports: [
    CommonModule,
    MatCardModule,
    RecipeComponent,
    MatGridListModule,
    MatIcon,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
  ],
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.scss',
})
export class RecipeListComponent {
  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);
  private recipeService = inject(RecipeService);

  currentPage: number = 0;
  searchTerm: string = '';
  recipeData: Page<Recipe> | null = null;

  searchControl = new FormControl('');

  ngOnInit(): void {
    this.loadRecipes();

    this.recipeStateService.recipeUpdated$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((updatedRecipe) => {
        if (updatedRecipe) {
          this.loadRecipes();
        }
      });

    this.recipeStateService.recipeDeleted$;
    this.recipeStateService.recipeDeleted$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadRecipes());

    this.searchControl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((value) => {
        this.searchTerm = value || '';
        this.currentPage = 0;
        this.loadRecipes();
      });
  }

  loadRecipes(): void {
    this.recipeService
      .getPublishedRecipes(this.currentPage, 12, this.searchTerm)
      .subscribe({
        next: (data) => {
          this.recipeData = data;
        },
      });
  }

  nextPage(): void {
    if (!this.recipeData?.last) {
      this.currentPage++;
      this.loadRecipes();
    }
  }

  previousPage(): void {
    if (!this.recipeData?.first) {
      this.currentPage--;
      this.loadRecipes();
    }
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.currentPage = 0;
    this.loadRecipes();
  }
}
