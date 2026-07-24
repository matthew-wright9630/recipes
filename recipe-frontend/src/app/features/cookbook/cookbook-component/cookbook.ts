import { Component, inject } from '@angular/core';
import { CookbookService } from '../cookbook.service';
import { FormControl } from '@angular/forms';
import { Page } from '../../../shared/models/page';
import { Cookbook } from '../../../shared/models/cookbook';
import { MatDialog } from '@angular/material/dialog';
import { CookbookCreateDialog } from '../../../shared/dialogs/cookbook-create-dialog/cookbook-create-dialog';
import { MatIcon } from '@angular/material/icon';

@Component({
  selector: 'app-cookbook',
  imports: [MatIcon],
  templateUrl: './cookbook.html',
  styleUrl: './cookbook.scss',
})
export class CookbookComponent {
  private cookbookService = inject(CookbookService);
  private dialog = inject(MatDialog);

  currentPage: number = 0;
  searchTerm: string = '';
  cookbookData: Page<Cookbook> | null = null;

  searchControl = new FormControl('');

  ngOnInit(): void {
    this.loadCookbooks();
    console.log(this.cookbookData);
  }

  loadCookbooks(): void {
    this.cookbookService
      .getAllAccessibleCookbooks(this.currentPage, 12, this.searchTerm)
      .subscribe({
        next: (data) => {
          this.cookbookData = data;
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
}
