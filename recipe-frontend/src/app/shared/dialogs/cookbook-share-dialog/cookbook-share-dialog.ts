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
import {
  MAT_DIALOG_DATA,
  MatDialogActions,
  MatDialogContent,
  MatDialogRef,
} from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatOption, MatSelect } from '@angular/material/select';
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
import { environment } from '../../../../environments/environment';

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
    MatDialogActions,
    MatDialogContent,
  ],
  templateUrl: './cookbook-share-dialog.html',
  styleUrl: './cookbook-share-dialog.scss',
})
export class CookbookShareDialog {
  private fb = inject(FormBuilder);
  private recipeService = inject(RecipeService);
  private cookbookService = inject(CookbookService);
  private cookbookStateService = inject(CookbookStateService);
  private userService = inject(UserService);

  private dialogRef = inject(MatDialogRef);
  private destroyRef = inject(DestroyRef);
  private snackbar = inject(MatSnackBar);
  data = inject<CookbookDetailInterface>(MAT_DIALOG_DATA);

  displayedColumns = ['user', 'remove'];
  shareRecipients: ShareRecipient[] = [];
  searchControl = new FormControl('');
  searchResults: UserSummary[] = [];
  sharedUsers: SharedUser[] = [];
  imageBaseUrl = environment.imageBaseUrl;

  form = this.fb.group({
    username: [''],
    email: [''],
  });

  onClose(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
    this.findSharedUsers();

    this.searchControl.valueChanges
      .pipe(
        debounceTime(300),
        distinctUntilChanged(),
        takeUntilDestroyed(this.destroyRef),
      )
      .subscribe((value) => {
        // const username = value?.trim() ?? '';

        if (!value) {
          this.searchResults = [];
          return;
        }

        this.userService.searchUsers(value).subscribe({
          next: (users) => {
            this.searchResults = users;
          },
          error: () => {
            this.searchResults = [];
          },
        });
      });
  }

  findSharedUsers(): void {
    this.cookbookService
      .getSharedUsers(this.data.id)
      .subscribe((response: SharedUser[]) => {
        this.sharedUsers = response;
      });
  }

  addUserId(user: UserSummary): void {
    const alreadyHasAccess = this.sharedUsers.some(
      (sharedUser) =>
        sharedUser.userId === user.id && sharedUser.permission !== 'REVOKED',
    );

    const accessRevoked = this.sharedUsers.some(
      (sharedUser) =>
        sharedUser.userId === user.id && sharedUser.permission === 'REVOKED',
    );

    const alreadySelected = this.shareRecipients.some(
      (recipient) => recipient.userId === user.id,
    );

    console.log(accessRevoked);

    if (alreadyHasAccess || alreadySelected) {
      this.snackbar.open(
        alreadyHasAccess
          ? 'User already has access to this cookbook'
          : 'User is already selected',
        'Dismiss',
        { duration: 5000 },
      );
    } else if (accessRevoked) {
      this.snackbar.open(
        'Access for this user has been revoked. Please contact the cookbook owner to update',
        'Dismiss',
        { duration: 5000 },
      );
    } else {
      this.shareRecipients = [
        ...this.shareRecipients,
        {
          userId: user.id,
          username: user.username,
          avatarUrl: user.avatarUrl,
        },
      ];
    }

    this.searchControl.setValue('');
  }

  addEmail(): void {
    const email = this.form.controls.email.value;

    if (!email) {
      return;
    }

    this.shareRecipients = [...this.shareRecipients, { email }];
    this.form.controls.email.reset();
  }

  removeRecipient(recipient: ShareRecipient): void {
    this.shareRecipients = this.shareRecipients.filter((r) => r !== recipient);
  }

  getInitials(name: string): string {
    if (!name) return '';

    const parts = name.trim().split(' ');

    if (parts.length === 1) {
      return parts[0].charAt(0).toUpperCase();
    }

    return (
      parts[0].charAt(0) + parts[parts.length - 1].charAt(0)
    ).toUpperCase();
  }

  getAvatarColor(name: string): string {
    let hash = 0;

    for (let i = 0; i < name.length; i++) {
      hash = name.charCodeAt(i) + ((hash << 5) - hash);
    }

    const colors = [
      '#1976d2',
      '#388e3c',
      '#f57c00',
      '#7b1fa2',
      '#c2185b',
      '#455a64',
    ];

    return colors[Math.abs(hash) % colors.length];
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

    this.cookbookService.shareCookbook(this.data.id, request).subscribe({
      next: () => {
        this.dialogRef.close();
        this.snackbar.open('Your request(s) have been sent!', 'Dismiss', {
          duration: 5000,
        });
      },
    });
  }
}
