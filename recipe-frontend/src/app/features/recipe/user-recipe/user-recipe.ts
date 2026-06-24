import { Component, computed, effect, inject, signal } from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { RecipeService } from '../../../shared/services/recipe-service/recipe.service';
import { MatIcon } from '@angular/material/icon';
import { MatDialog } from '@angular/material/dialog';
import { RecipeCreateDialog } from '../../../shared/dialogs/recipe-create-dialog/recipe-create-dialog';

@Component({
  selector: 'app-user-recipe',
  imports: [RecipeComponent, MatIcon],
  templateUrl: './user-recipe.html',
  styleUrl: './user-recipe.scss',
})
export class UserRecipe {
  recipeList = signal<Recipe[]>([]);

  private dialog = inject(MatDialog);

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

  openCreateRecipe() {
    this.dialog.open(RecipeCreateDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }
}
