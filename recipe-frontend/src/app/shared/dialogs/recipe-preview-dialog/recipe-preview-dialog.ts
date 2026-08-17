import { Component, computed, inject } from '@angular/core';
import {
  MAT_DIALOG_DATA,
  MatDialog,
  MatDialogActions,
  MatDialogContent,
  MatDialogModule,
  MatDialogTitle,
} from '@angular/material/dialog';
import { RouterLink } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthStateService } from '../../services//auth-state-service/auth-state.service';
import { RecipeEditDialog } from '../recipe-edit-dialog/recipe-edit-dialog';
import { ConfirmationDialog } from '../confirmation-dialog/confirmation-dialog';
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { RecipeStateService } from '../../services/recipe-state-service/recipe-state.service';
import { MatIcon } from '@angular/material/icon';
import { RecipeLikeService } from '../../services/recipe-like-service/recipe-like.service';
import { environment } from '../../../../environments/environment';
import { MatButtonModule } from '@angular/material/button';
import {
  MatMenu,
  MatMenuContent,
  MatMenuItem,
  MatMenuModule,
  MatMenuTrigger,
} from '@angular/material/menu';
import { MatSnackBar, MatSnackBarModule } from '@angular/material/snack-bar';
import { Recipe } from '../../models/recipe';
import { CookbookRecipeDialog } from '../cookbook-recipe-dialog/cookbook-recipe-dialog';
import { CookbookStateService } from '../../services/cookbook-state/cookbook-state.service';
import { Cookbook } from '../../models/cookbook';
import { AuthPromptService } from '../../services/auth-prompt-service/auth-prompt.service';

@Component({
  selector: 'app-recipe-preview-dialog',
  imports: [
    MatDialogModule,
    MatDialogContent,
    MatDialogTitle,
    RouterLink,
    CommonModule,
    MatIcon,
    MatButtonModule,
    MatMenu,
    MatMenuItem,
    MatMenuTrigger,
    MatMenuModule,
    MatSnackBarModule,
  ],
  templateUrl: './recipe-preview-dialog.html',
  styleUrl: './recipe-preview-dialog.scss',
})
export class RecipePreviewDialog {
  recipe = inject(MAT_DIALOG_DATA);
  recipeService = inject(RecipeService);
  recipeStateService = inject(RecipeStateService);
  cookbookStateService = inject(CookbookStateService);
  recipeLikeService = inject(RecipeLikeService);
  authState = inject(AuthStateService);
  authPrompt = inject(AuthPromptService);
  imageUrl: string = environment.imageBaseUrl + 'recipes/';
  frontendUrl: string = environment.baseFrontendUrl;
  backendUrl: string = environment.apiUrl;

  private dialog = inject(MatDialog);
  private snackbar = inject(MatSnackBar);

  isOwner = computed(
    () => this.authState.currentUser()?.id === this.recipe.createdById,
  );

  sortedIngredients = computed(() =>
    [...this.recipe.recipeIngredients].sort(
      (a, b) => a.sortOrder - b.sortOrder,
    ),
  );

  sortedDirections = computed(() =>
    [...this.recipe.recipeDirections].sort((a, b) => a.sortOrder - b.sortOrder),
  );

  getWholeNumber(quantity: number): number {
    return Math.floor(quantity);
  }

  getFraction(quantity: number): string {
    const decimal = quantity % 1;

    switch (decimal) {
      case 0.125:
        return '⅛';
      case 0.25:
        return '¼';
      case 0.333:
        return '⅓';
      case 0.5:
        return '½';
      case 0.667:
        return '⅔';
      case 0.75:
        return '¾';
      default:
        return '';
    }
  }

  openEditDialog() {
    const raw = localStorage.getItem(`recipe-draft-${this.recipe.id}`);
    let data = this.recipe;

    if (raw) {
      const draft = JSON.parse(raw);
      const savedAt = new Date(draft.savedAt);
      const daysSince =
        (Date.now() - savedAt.getTime()) / (1000 * 60 * 60 * 24);

      if (daysSince <= 7) {
        data = { ...this.recipe, ...draft.values };
      } else {
        localStorage.removeItem(`recipe-draft-${this.recipe.id}`);
      }
    }

    this.dialog.open(RecipeEditDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
      data,
    });
  }

  onRevise(): void {
    this.recipeService.reviseRecipe(this.recipe.id).subscribe({
      next: (result) => {
        if (result) {
          this.dialog.closeAll();
          this.recipeStateService.notifyRecipeUpdated(result);
          this.dialog.open(RecipeEditDialog, {
            width: '800px',
            maxWidth: '95vw',
            autoFocus: false,
            data: result,
          });
        }
      },
      error: (err) => console.error(err),
    });
  }

  onArchive(): void {
    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Archive ${this.recipe.name}`,
        message:
          "This will hide your recipe from search and other users' cookbooks. You can revise this recipe later to publish it.",
        confirmLabel: 'Archive',
        confirmColor: 'warn',
      },
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.recipeService.archiveRecipe(this.recipe.id).subscribe({
        next: (result) => {
          if (result) {
            this.recipeStateService.notifyRecipeUpdated(result);
          }
          this.dialog.closeAll();
        },
        error: (err) => console.error(err),
      });
    });
  }

  onDelete(): void {
    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Delete ${this.recipe.name}`,
        message:
          'This will permanently delete your draft. This action is not reversable.',
        confirmLabel: 'Delete',
        confirmColor: 'warn',
      },
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.recipeService.deleteDraftRecipe(this.recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeDeleted(this.recipe.id);
          this.dialog.closeAll();
        },
        error: (err) => console.error(err),
      });
    });
  }

  toggleFavorite(): void {
    if (this.authState.currentUser()) {
      if (!this.recipe.likedByCurrentUser) {
        this.recipeLikeService.likeRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.likedByCurrentUser = true;
            this.recipeStateService.notifyRecipeUpdated(this.recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not save your like. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      } else {
        this.recipeLikeService.unlikeRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.likedByCurrentUser = false;
            this.recipeStateService.notifyRecipeUpdated(this.recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not remove your like. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      }
    } else {
      this.authPrompt.promptLogin('Login to like this recipe!', () => {
        this.recipeService.getRecipeById(this.recipe.id).subscribe({
          next: (updatedRecipe) => {
            if (updatedRecipe) {
              this.recipe.likedByCurrentUser = updatedRecipe.likedByCurrentUser;
              this.recipe.bookmarkedByCurrentUser =
                updatedRecipe.bookmarkedByCurrentUser;
              if (!updatedRecipe.likedByCurrentUser) {
                this.toggleFavorite();
              }
            }
          },
        });
      });
    }
  }

  toggleBookmark(recipe: Recipe): void {
    if (this.authState.currentUser()) {
      if (!this.recipe.bookmarkedByCurrentUser) {
        this.recipeService.bookmarkRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.bookmarkedByCurrentUser = true;
            this.recipeStateService.notifyRecipeUpdated(recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not save your bookmark. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      } else {
        this.recipeService.unBookmarkRecipe(this.recipe.id).subscribe({
          next: () => {
            this.recipe.bookmarkedByCurrentUser = false;
            this.recipeStateService.notifyRecipeUpdated(recipe);
          },
          error: (err) => {
            this.snackbar.open(
              'We could not remove your bookmark. Please try again.',
              'Dismiss',
              { duration: 5000 },
            );
          },
        });
      }
    } else {
      this.authPrompt.promptLogin('Login to bookmark this recipe!', () => {
        this.recipeService.getRecipeById(this.recipe.id).subscribe({
          next: (updatedRecipe) => {
            if (updatedRecipe) {
              this.recipe.likedByCurrentUser = updatedRecipe.likedByCurrentUser;
              this.recipe.bookmarkedByCurrentUser =
                updatedRecipe.bookmarkedByCurrentUser;
              if (!updatedRecipe.bookmarkedByCurrentUser) {
                this.toggleFavorite();
              }
            }
          },
        });
      });
    }
  }

  moreActions = [
    { label: 'Copy Link', action: (recipe: Recipe) => this.copyLink(recipe) },
    { label: 'Share', action: (recipe: Recipe) => this.shareRecipe(recipe) },
    {
      label: 'Open as PDF',
      action: (recipe: Recipe) => this.openAsPdf(recipe),
    },
  ];

  copyLink(recipe: Recipe): void {
    navigator.clipboard.writeText(this.frontendUrl + '/recipe/' + recipe.id);

    this.snackbar.open('Recipe link copied!', 'Close', {
      duration: 3000,
    });
  }

  shareRecipe(recipe: Recipe): void {
    const url = this.frontendUrl + '/recipe/' + recipe.id;

    if (navigator.share) {
      navigator.share({
        title: recipe.name,
        text: `Check out this recipe: ${recipe.name}`,
        url,
      });
    } else {
      const subject = `Check out this recipe: ${recipe.name}`;
      const body = `I thought you might like this recipe:\n\n${url}`;

      const mailto = `mailto:?subject=${encodeURIComponent(subject)}&body=${encodeURIComponent(body)}`;

      window.location.href = mailto;
    }
  }

  openAsPdf(recipe: Recipe): void {
    this.recipeService.downloadRecipePdf(recipe.id).subscribe((blob) => {
      const url = URL.createObjectURL(blob);

      window.open(url, '_blank');

      // Optional cleanup
      setTimeout(() => {
        URL.revokeObjectURL(url);
      }, 1000);
    });
  }

  onAddToCookbook(): void {
    if (this.authState.currentUser()) {
      const dialogRef = this.dialog.open(CookbookRecipeDialog, {
        width: '800px',
        maxWidth: '95vw',
        autoFocus: false,
        data: this.recipe,
      });

      dialogRef.afterClosed().subscribe((updatedCookbooks) => {
        if (updatedCookbooks) {
          updatedCookbooks.forEach((cookbook: Cookbook) => {
            this.cookbookStateService.notifyCookbookUpdated(cookbook);
          });
        }
      });
    } else {
      this.authPrompt.promptLogin('Login to bookmark this recipe!', () => {
        this.recipeService.getRecipeById(this.recipe.id).subscribe({
          next: (updatedRecipe) => {
            if (updatedRecipe) {
              this.recipe.likedByCurrentUser = updatedRecipe.likedByCurrentUser;
              this.recipe.bookmarkedByCurrentUser =
                updatedRecipe.bookmarkedByCurrentUser;
              if (!updatedRecipe.bookmarkedByCurrentUser) {
                this.toggleFavorite();
              }
            }
          },
        });
      });
    }
  }
}
