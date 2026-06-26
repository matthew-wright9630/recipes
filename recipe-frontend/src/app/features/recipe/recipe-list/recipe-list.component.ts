import { CommonModule } from '@angular/common';
import { Component, DestroyRef, effect, inject, signal } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { RecipeService } from '../../../shared/services/recipe-service/recipe.service';
import { RecipeStateService } from '../../../shared/services/recipe-state-service/recipe-state.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-recipe-list',
  imports: [CommonModule, MatCardModule, RecipeComponent, MatGridListModule],
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.scss',
})
export class RecipeListComponent {
  recipeList = signal<Recipe[]>([]);

  private recipeStateService = inject(RecipeStateService);
  private destroyRef = inject(DestroyRef);

  ngOnInit(): void {
    this.recipeStateService.recipeUpdated$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((updatedRecipe) => {
        if (updatedRecipe) {
          this.recipeService.getAllRecipes().subscribe((recipes) => {
            this.recipeList.set(recipes);
          });
        }
      });

    this.recipeStateService.recipeDeleted$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((deletedId) => {
        this.recipeList.update((recipes) =>
          recipes.filter((r) => r.id !== deletedId),
        );
      });
  }

  constructor(private recipeService: RecipeService) {
    effect(() => {
      this.recipeService.getAllRecipes().subscribe((recipes) => {
        this.recipeList.set(recipes);
      });
    });
  }
}
