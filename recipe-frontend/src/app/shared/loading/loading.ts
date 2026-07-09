import { CommonModule } from '@angular/common';
import { Component, inject } from '@angular/core';
import { LoadingService } from '../services/loading-service/loading.service';
import { MatProgressBarModule } from '@angular/material/progress-bar';

@Component({
  selector: 'app-loading',
  imports: [MatProgressBarModule, CommonModule],
  template: `
    @if (loadingService.isLoading()) {
      <mat-progress-bar
        mode="indeterminate"
        class="loading-bar"
      ></mat-progress-bar>
    }
  `,
  styles: [
    `
      .loading-bar {
        position: fixed;
        top: 0;
        left: 0;
        right: 0;
        z-index: 9999;
      }
    `,
  ],
})
export class LoadingComponent {
  loadingService = inject(LoadingService);
}
