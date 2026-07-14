import { CommonModule } from '@angular/common';
import {
  Component,
  effect,
  Inject,
  inject,
  Optional,
  signal,
} from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { RecipeService } from '../../../shared/services/recipe-service/recipe.service';
import { environment } from '../../../../environments/environment';
import { MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { MatMenu, MatMenuItem, MatMenuModule } from '@angular/material/menu';
import { MatIcon } from '@angular/material/icon';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RecipeLikeService } from '../../../shared/services/recipe-like-service/recipe-like.service';
import { RecipeStateService } from '../../../shared/services/recipe-state-service/recipe-state.service';
import { AuthStateService } from '../../../shared/services/auth-state-service/auth-state.service';

@Component({
  selector: 'app-recipe-detail',
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    MatButtonModule,
    MatMenu,
    MatMenuModule,
    MatMenuItem,
    MatIcon,
  ],
  templateUrl: './recipe-detail.html',
  styleUrl: './recipe-detail.scss',
})
export class RecipeDetail {
  private route = inject(ActivatedRoute);
  private recipeService = inject(RecipeService);
  private dialog = inject(MatDialog);
  private snackbar = inject(MatSnackBar);
  private recipeLikeService = inject(RecipeLikeService);
  private recipeStateService = inject(RecipeStateService);

  authState = inject(AuthStateService);

  recipe = signal<Recipe | null>(null);

  imageUrl: string = environment.imageBaseUrl + 'recipes/';
  frontendUrl: string = environment.baseFrontendUrl;

  constructor(
    @Optional() @Inject(MAT_DIALOG_DATA) dialogRecipe: Recipe | null,
  ) {}

  ngOnInit() {
    this.dialog.closeAll();
    const id = Number(this.route.snapshot.paramMap.get('id'));

    this.recipeService.getRecipeById(id).subscribe((recipe) => {
      if (recipe) {
        recipe.recipeIngredients.sort((a, b) => a.sortOrder - b.sortOrder);

        recipe.recipeDirections.sort((a, b) => a.stepNumber - b.stepNumber);

        this.recipe.set(recipe);
      }
    });
  }

  toggleFavorite(): void {
    const recipe = this.recipe();

    if (!recipe) {
      return;
    }

    if (!recipe.likedByCurrentUser) {
      this.recipeLikeService.likeRecipe(recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeUpdated(recipe);
          this.recipe.update((r) => ({
            ...r!,
            likedByCurrentUser: true,
          }));
        },
      });
    } else {
      this.recipeLikeService.unlikeRecipe(recipe.id).subscribe({
        next: () => {
          this.recipeStateService.notifyRecipeUpdated(recipe);
          this.recipe.update((r) => ({
            ...r!,
            likedByCurrentUser: false,
          }));
        },
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
}
