import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatInput } from '@angular/material/input';
import { MatFormField, MatLabel } from '@angular/material/form-field';
import { MatListModule } from '@angular/material/list';
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { CookbookService } from '../../services/cookbook-service/cookbook.service';
import { CookbookStateService } from '../../services/cookbook-state/cookbook-state.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import {
  MatOption,
  MatSelect,
  MatSelectModule,
} from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { SharedUser } from '../../models/shared-user';
import { CookbookDetailInterface } from '../../models/cookbook-detail-interface';
import { UserService } from '../../services/user-service/user.service';
import { UserSummary } from '../../models/user-summary';
import { ShareRecipient } from '../../models/share-recipient';
import {
  MatAutocomplete,
  MatAutocompleteModule,
} from '@angular/material/autocomplete';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';

@Component({
  selector: 'app-cookbook-share-dialog',
  imports: [
    MatIcon,
    MatListModule,
    MatFormField,
    MatLabel,
    ReactiveFormsModule,
    MatButtonModule,
    MatInput,
    MatOption,
    MatTableModule,
    MatAutocompleteModule,
    MatSelectModule,
  ],
  templateUrl: './cookbook-manage-access-dialog.html',
  styleUrl: './cookbook-manage-access-dialog.scss',
})
export class CookbookManageAccessDialog {
  private fb = inject(FormBuilder);
  private recipeService = inject(RecipeService);
  private cookbookService = inject(CookbookService);
  private cookbookStateService = inject(CookbookStateService);
  private userService = inject(UserService);

  private dialogRef = inject(MatDialogRef);
  private destroyRef = inject(DestroyRef);
  private snackbar = inject(MatSnackBar);
  data = inject<CookbookDetailInterface>(MAT_DIALOG_DATA);

  displayedColumns = ['user', 'access', 'remove'];
  shareRecipients: ShareRecipient[] = [];
  searchControl = new FormControl('');
  searchResults: UserSummary[] = [];

  sharedUsers: SharedUser[] = [];

  form = this.fb.group({
    username: [''],
    email: [''],
  });

  onClose(): void {
    this.dialogRef.close();
  }

  removeRecipient(recipient: ShareRecipient): void {
    this.shareRecipients = this.shareRecipients.filter((r) => r !== recipient);
  }

  ngOnInit(): void {
    this.findSharedUsers();
  }

  findSharedUsers(): void {
    this.cookbookService
      .getSharedUsers(this.data.id)
      .subscribe((response: SharedUser[]) => {
        this.sharedUsers = response.filter(
          (user) => user.permission !== 'OWNER',
        );
      });
  }

  shareCookbook(): void {
    const userIds = this.shareRecipients
      .filter((recipient) => recipient.userId !== undefined)
      .map((recipient) => recipient.userId!);

    const emails = this.shareRecipients
      .filter((recipient) => recipient.email !== undefined)
      .map((recipient) => recipient.email!);

    const request = {
      userIds,
      emails,
    };

    this.cookbookService.updateAccess(this.data.id, request).subscribe({
      next: () => {
        this.dialogRef.close();
        this.snackbar.open('Your request(s) have been sent!', 'Dismiss');
      },
    });
  }
}
