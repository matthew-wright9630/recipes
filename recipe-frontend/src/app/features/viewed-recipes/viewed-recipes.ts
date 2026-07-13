import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { RecipeComponent } from '../../shared/components/recipe-card/recipe-card.component';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatIcon } from '@angular/material/icon';
import { ReactiveFormsModule } from '@angular/forms';
import { RecipeStateService } from '../../shared/services/recipe-state-service/recipe-state.service';
import { RecipeService } from '../../shared/services/recipe-service/recipe.service';
import { Recipe } from '../../shared/models/recipe';
import { Page } from '../../shared/models/page';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-viewed-recipes',
  imports: [
    CommonModule,
    MatCardModule,
    RecipeComponent,
    MatGridListModule,
    MatIcon,
    ReactiveFormsModule,
  ],
  templateUrl: './viewed-recipes.html',
  styleUrl: './viewed-recipes.scss',
})
export class ViewedRecipes {
  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);
  private recipeService = inject(RecipeService);

  currentPage: number = 0;
  searchTerm: string = '';
  recipeData: Page<Recipe> | null = null;

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
  }

  loadRecipes(): void {
    this.recipeService.getRecipeViewHistory(this.currentPage, 12).subscribe({
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
}
