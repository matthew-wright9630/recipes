import { CommonModule } from '@angular/common';
import { Component, DestroyRef, inject, signal } from '@angular/core';
import { MatButtonModule } from '@angular/material/button';
import { MatCardModule } from '@angular/material/card';
import { MatChipsModule } from '@angular/material/chips';
import { MatDialog } from '@angular/material/dialog';
import { MatDividerModule } from '@angular/material/divider';
import { MatMenuModule } from '@angular/material/menu';
import { AuthStateService } from '../../../shared/services/auth-state-service/auth-state.service';
import { Cookbook } from '../../../shared/models/cookbook';
import { environment } from '../../../../environments/environment';
import { CookbookService } from '../../../shared/services/cookbook-service/cookbook.service';
import { ActivatedRoute, Router } from '@angular/router';
import { CookbookDetailInterface } from '../../../shared/models/cookbook-detail-interface';
import { RecipeComponent } from '../../../shared/components/recipe-card/recipe-card.component';
import { CookbookStateService } from '../../../shared/services/cookbook-state/cookbook-state.service';
import { CookbookEditDialog } from '../../../shared/dialogs/cookbook-edit-dialog/cookbook-edit-dialog';
import { RecipeStateService } from '../../../shared/services/recipe-state-service/recipe-state.service';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { MatSnackBar } from '@angular/material/snack-bar';
import { RecipeService } from '../../../shared/services/recipe-service/recipe.service';
import { ConfirmationDialog } from '../../../shared/dialogs/confirmation-dialog/confirmation-dialog';
import { CookbookShareDialog } from '../../../shared/dialogs/cookbook-share-dialog/cookbook-share-dialog';
import { CookbookManageAccessDialog } from '../../../shared/dialogs/cookbook-manage-access-dialog/cookbook-manage-access-dialog';

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
  private router = inject(Router);
  private dialog = inject(MatDialog);
  private destroyRef = inject(DestroyRef);
  private cookbookService = inject(CookbookService);
  private recipeService = inject(RecipeService);
  private cookbookStateService = inject(CookbookStateService);
  private recipeStateService = inject(RecipeStateService);
  private snackbar = inject(MatSnackBar);

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

    this.recipeStateService.recipeUpdated$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe((updatedRecipe) => {
        if (updatedRecipe) {
          this.loadCookbook();
        }
        console.log(this.cookbook()?.type === 'BOOKMARK', updatedRecipe);
        if (
          this.cookbook()?.type === 'BOOKMARK' &&
          !updatedRecipe.bookmarkedByCurrentUser
        ) {
          const snackBarRef = this.snackbar.open(
            updatedRecipe.name + ' removed from bookmarks.',
            'Undo',
            { duration: 5000 },
          );

          snackBarRef.onAction().subscribe(() => {
            this.recipeService.bookmarkRecipe(updatedRecipe.id).subscribe({
              next: () => {
                updatedRecipe.bookmarkedByCurrentUser = true;
                this.recipeStateService.notifyRecipeUpdated(updatedRecipe);
              },
              error: (err) => {
                this.snackbar.open(
                  'We could not save your bookmark. Please try again.',
                  'Dismiss',
                  { duration: 5000 },
                );
              },
            });
          });
        }
      });

    this.recipeStateService.recipeDeleted$;
    this.recipeStateService.recipeDeleted$
      .pipe(takeUntilDestroyed(this.destroyRef))
      .subscribe(() => this.loadCookbook());
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

  onCookbookShare(): void {
    this.dialog.open(CookbookShareDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
      data: this.cookbook(),
    });
  }

  onManageAccess(): void {
    this.dialog.open(CookbookManageAccessDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
      data: this.cookbook(),
    });
  }

  onDelete(): void {
    const confirmRef = this.dialog.open(ConfirmationDialog, {
      data: {
        title: `Delete ${this.cookbook.name}`,
        message:
          'This will permanently delete your cookbook. If this is shared with other users, they will not be able to access this cookbook. This action is not reversable.',
        confirmLabel: 'Delete',
        confirmColor: 'warn',
      },
    });

    confirmRef.afterClosed().subscribe((confirmed) => {
      if (!confirmed) return;
      this.cookbookService.deleteDraftRecipe(this.cookbook()!.id).subscribe({
        next: () => {
          this.cookbookStateService.notifyCookbookDeleted(this.cookbook()!.id);
          this.router.navigate(['/cookbooks']);
          this.dialog.closeAll();
        },
        error: (err) => console.error(err),
      });
    });
  }
}
