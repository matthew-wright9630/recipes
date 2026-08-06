import { Component, DestroyRef, inject } from '@angular/core';
import { CookbookService } from '../cookbook.service';
import { FormControl, ReactiveFormsModule } from '@angular/forms';
import { Page } from '../../../shared/models/page';
import { Cookbook } from '../../../shared/models/cookbook';
import { MatDialog } from '@angular/material/dialog';
import { CookbookCreateDialog } from '../../../shared/dialogs/cookbook-create-dialog/cookbook-create-dialog';
import { MatIcon } from '@angular/material/icon';
import { MatFormField, MatInput, MatLabel } from '@angular/material/input';
import { MatGridListModule } from '@angular/material/grid-list';
import { MatCardModule } from '@angular/material/card';
import { CommonModule } from '@angular/common';
import { CookbookCard } from '../../../shared/components/cookbook-card/cookbook-card';
import { MatButtonModule } from '@angular/material/button';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-cookbook',
  imports: [
    CommonModule,
    MatCardModule,
    MatGridListModule,
    MatIcon,
    ReactiveFormsModule,
    MatFormField,
    MatLabel,
    MatInput,
    CookbookCard,
    MatButtonModule,
  ],
  templateUrl: './cookbook.html',
  styleUrl: './cookbook.scss',
})
export class CookbookComponent {
  private cookbookService = inject(CookbookService);
  private dialog = inject(MatDialog);
  private destroyRef = inject(DestroyRef);

  currentPage: number = 0;
  searchTerm: string = '';
  cookbookData: Page<Cookbook> | null = null;
  errorMessage: string = '';

  searchControl = new FormControl('', { nonNullable: true });

  ngOnInit(): void {
    this.loadCookbooks();

    this.searchControl.valueChanges
      .pipe(
        debounceTime(500),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((value) => {
        this.searchTerm = value || '';
        this.currentPage = 0;
        this.loadCookbooks();
      });
  }

  loadCookbooks(): void {
    this.cookbookService
      .getAllAccessibleCookbooks(this.currentPage, 12, this.searchControl.value)
      .subscribe({
        next: (data) => {
          this.cookbookData = data;
          this.errorMessage = '';
        },
        error: () => {
          this.errorMessage = 'We could not load your cookbooks';
        },
      });
  }

  openCreateCookbook() {
    this.dialog.open(CookbookCreateDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  nextPage(): void {
    if (!this.cookbookData?.last) {
      this.currentPage++;
      this.loadCookbooks();
    }
  }

  previousPage(): void {
    if (!this.cookbookData?.first) {
      this.currentPage--;
      this.loadCookbooks();
    }
  }

  onSearch(event: Event): void {
    this.searchTerm = (event.target as HTMLInputElement).value;
    this.currentPage = 0;
    this.loadCookbooks();
  }
}
