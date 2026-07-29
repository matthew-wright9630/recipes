import { CommonModule } from '@angular/common';
import { Component, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { AuthStateService } from '../../../shared/services/auth-state-service/auth-state.service';
import { Cookbook } from '../../../shared/models/cookbook';
import { environment } from '../../../../environments/environment';
import { CookbookService } from '../cookbook.service';
import { ActivatedRoute } from '@angular/router';
import { CookbookDetailInterface } from '../../../shared/models/cookbook-detail-interface';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { CookbookStateService } from '../../../shared/services/cookbook-state/cookbook-state.service';
import { CookbookEditDialog } from '../../../shared/dialogs/cookbook-edit-dialog/cookbook-edit-dialog';

@Component({
  selector: 'app-cookbook-detail',
  imports: [
    CommonModule,
    MatCardModule,
    MatChipsModule,
    MatDividerModule,
    MatButtonModule,
    MatMenuModule,
    RecipeComponent,
  ],
  templateUrl: './cookbook-detail.html',
  styleUrl: './cookbook-detail.scss',
})
export class CookbookDetail {
  private route = inject(ActivatedRoute);
  private dialog = inject(MatDialog);
  private cookbookService = inject(CookbookService);
  private cookbookStateService = inject(CookbookStateService);

  authState = inject(AuthStateService);
  cookbook = signal<CookbookDetailInterface | null>(null);

  imageUrl: string = environment.imageBaseUrl + 'recipes/';
  frontendUrl: string = environment.baseFrontendUrl;

  private cookbookId!: number;

  ngOnInit() {
    this.cookbookId = Number(this.route.snapshot.paramMap.get('id'));

    this.loadCookbook();

    this.cookbookStateService.cookbookUpdated$.subscribe(() => {
      this.loadCookbook();
    });
  }

  private loadCookbook(): void {
    this.cookbookService
      .getCookbookById(this.cookbookId)
      .subscribe((response) => {
        this.cookbook.set(response);
      });
  }

  onEditCookbook(): void {
    this.dialog.open(CookbookEditDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
      data: this.cookbook(),
    });
  }
}
