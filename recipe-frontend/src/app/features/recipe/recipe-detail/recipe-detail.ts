import { CommonModule } from '@angular/common';
import { Component, effect, inject, signal } from '@angular/core';
import { Recipe } from '../../../shared/models/recipe';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { ActivatedRoute } from '@angular/router';
import { MatCardModule } from '@angular/material/card';
import { MatButtonModule } from '@angular/material/button';
import { MatChipsModule } from '@angular/material/chips';
import { MatDividerModule } from '@angular/material/divider';
import { RecipeService } from '../../../shared/services/recipe.service';

@Component({
  selector: 'app-recipe-detail',
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    MatButtonModule,
  ],
  templateUrl: './recipe-detail.html',
  styleUrl: './recipe-detail.scss',
})
export class RecipeDetail {
  private route = inject(ActivatedRoute);

  recipe = signal<Recipe | null>(null);

  constructor(private recipeService: RecipeService) {
    effect(() => {
      const id = Number(this.route.snapshot.paramMap.get('id'));

      this.recipeService.getRecipeById(id).subscribe((recipe) => {
        recipe?.recipeIngredients.sort((a, b) => a.sortOrder - b.sortOrder);
        recipe?.recipeDirections.sort((a, b) => a.stepNumber - b.stepNumber);
        this.recipe.set(recipe);
      });
    });
  }
}
