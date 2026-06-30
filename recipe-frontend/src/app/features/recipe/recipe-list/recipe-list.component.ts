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

@Component({
  selector: 'app-recipe-list',
  imports: [
    CommonModule,
    MatCardModule,
    RecipeComponent,
    MatGridListModule,
    MatIcon,
  ],
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.scss',
})
export class RecipeListComponent {
  // recipeList = signal<Recipe[]>([]);

  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);
  private recipeService = inject(RecipeService);

  currentPage = 0;
  recipeData: Page<Recipe> | null = null;

  ngOnInit(): void {
    console.log('NG INIT');
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
    console.log('Load Recipe');
    this.recipeService.getPublishedRecipes(this.currentPage).subscribe({
      next: (data) => {
        console.log(data);
        this.recipeData = data;
      },
    });
  }

  test(): void {
    console.log('Test');
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
