import { Component, Inject, inject, Input } from '@angular/core';
import { AuthStateService } from '../../services/auth-state-service/auth-state.service';
import { CookbookStateService } from '../../services/cookbook-state/cookbook-state.service';
import { environment } from '../../../../environments/environment';
import { MatIcon } from '@angular/material/icon';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { Cookbook } from '../../models/cookbook';
import { Router, RouterLink } from '@angular/router';

@Component({
  selector: 'app-cookbook-card',
  imports: [CommonModule, MatCardModule],
  templateUrl: './cookbook-card.html',
  styleUrl: './cookbook-card.scss',
})
export class CookbookCard {
  @Input() cookbook!: Cookbook;
  authState = inject(AuthStateService);
  cookbookStateService = inject(CookbookStateService);
  backendUrl: string = environment.apiUrl + '/uploads/';
  imageUrl: string = environment.imageBaseUrl + 'recipes/';
  router = inject(Router);

  routeToCookbook(): void {
    this.router.navigate(['/cookbooks', this.cookbook.id]);
  }
}
