import { CommonModule } from '@angular/common';
import { Component, Input } from '@angular/core';
import { MatCardModule } from '@angular/material/card';
import { Recipe } from '../../models/recipe'

@Component({
  selector: 'app-recipe',
  standalone: true,
  imports: [CommonModule, MatCardModule],
  templateUrl: './recipe-card.component.html',
  styleUrl: './recipe-card.component.scss'
})
export class RecipeComponent {
  @Input() recipe!: Recipe
  
  
}
