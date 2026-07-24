import { Component, inject } from '@angular/core';
import { CookbookService } from '../cookbook.service';
import { FormControl } from '@angular/forms';
import { Page } from '../../../shared/models/page';
import { Cookbook } from '../../../shared/models/cookbook';

@Component({
  selector: 'app-cookbook',
  imports: [],
  templateUrl: './cookbook.html',
  styleUrl: './cookbook.scss',
})
export class CookbookComponent {
  private cookbookService = inject(CookbookService);

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
}
