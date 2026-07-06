import { CommonModule } from '@angular/common';
import { Component, inject, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Recipe } from '../../models/recipe';
import { MatDialog } from '@angular/material/dialog';
import { RecipePreviewDialog } from '../../dialogs/recipe-preview-dialog/recipe-preview-dialog';
import { AuthStateService } from '../../services/auth-state-service/auth-state.service';
import { RecipeStateService } from '../../services/recipe-state-service/recipe-state.service';
import { RecipeLikeService } from '../../services/recipe-like-service/recipe-like.service';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-recipe',
  standalone: true,
  imports: [CommonModule, MatCardModule, MatIcon],
  templateUrl: './recipe-card.component.html',
  styleUrl: './recipe-card.component.scss',
})
export class RecipeComponent {
  @Input() recipe!: Recipe;
  private dialog = inject(MatDialog);
  authState = inject(AuthStateService);
  recipeStateService = inject(RecipeStateService);
  recipeLikeService = inject(RecipeLikeService);
  backendUrl: string = 'http://localhost:8083/uploads/';

  constructor() {}
  openRecipe(recipe: Recipe) {
    this.dialog.open(RecipePreviewDialog, {
      data: recipe,
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  toggleFavorite(recipe: Recipe): void {
    if (!this.recipe.likedByCurrentUser) {
      this.recipeLikeService.likeRecipe(this.recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeUpdated(recipe);
          this.recipe.likedByCurrentUser = true;
        },
        error: (err) => console.error(err),
      });
    } else {
      this.recipeLikeService.unlikeRecipe(this.recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeUpdated(recipe);
          this.recipe.likedByCurrentUser = false;
        },
        error: (err) => console.error(err),
      });
    }
  }
}
