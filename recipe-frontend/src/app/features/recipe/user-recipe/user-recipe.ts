import { Component, computed, effect, signal } from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeService } from '../recipe.service';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';

@Component({
  selector: 'app-user-recipe',
  imports: [RecipeComponent],
  templateUrl: './user-recipe.html',
  styleUrl: './user-recipe.scss',
})
export class UserRecipe {
  recipeList = signal<Recipe[]>([]);

  draftRecipes = computed(() =>
    this.recipeList()
      .filter((r) => r.status === 'DRAFT')
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      ),
  );

  publishedRecipes = computed(() =>
    this.recipeList()
      .filter((r) => r.status === 'PUBLISHED')
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      ),
  );

  archivedRecipes = computed(() =>
    this.recipeList()
      .filter((r) => r.status === 'ARCHIVED')
      .sort(
        (a, b) =>
          new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
      ),
  );

  constructor(private recipeService: RecipeService) {
    effect(() => {
      this.recipeService.getRecipesByUser().subscribe((recipes) => {
        const sortedRecipes = [...recipes].sort(
          (a, b) =>
            new Date(b.createdAt).getTime() - new Date(a.createdAt).getTime(),
        );

        this.recipeList.set(sortedRecipes);
      });
    });
  }
}
