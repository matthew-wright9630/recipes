import { CommonModule } from '@angular/common';
import { Component, effect, signal } from '@angular/core';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { RecipeService } from '../recipe.service';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { ActivatedRoute } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from '../../../shared/dialogs/login-dialog/login-dialog';

@Component({
  selector: 'app-recipe-list',
  imports: [CommonModule, MatCardModule, RecipeComponent, MatGridListModule],
  templateUrl: './recipe-list.component.html',
  styleUrl: './recipe-list.component.scss',
})
export class RecipeListComponent {
  recipeList = signal<Recipe[]>([]);

  constructor(
    private recipeService: RecipeService,
    private route: ActivatedRoute,
    private dialog: MatDialog,
  ) {
    effect(() => {
      this.recipeService.getAllRecipes().subscribe((recipes) => {
        this.recipeList.set(recipes);
      });
    });
  }
}
