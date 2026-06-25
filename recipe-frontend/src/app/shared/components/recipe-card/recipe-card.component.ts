import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Recipe } from '../../models/recipe';
import { MatDialog } from '@angular/material/dialog';
import { RecipePreviewDialog } from '../../dialogs/recipe-preview-dialog/recipe-preview-dialog';

@Component({
  selector: 'app-recipe',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './recipe-card.component.html',
  styleUrl: './recipe-card.component.scss',
})
export class RecipeComponent {
  @Input() recipe!: Recipe;

  constructor(private dialog: MatDialog) {}
  openRecipe(recipe: Recipe) {
    this.dialog.open(RecipePreviewDialog, {
      data: recipe,
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }
}
