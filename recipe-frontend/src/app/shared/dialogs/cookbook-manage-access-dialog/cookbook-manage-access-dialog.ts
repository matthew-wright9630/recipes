import { Component, DestroyRef, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import { MatFormField } from '@angular/material/form-field';
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
import { MatOption, MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { SharedUser } from '../../models/shared-user';
import { CookbookDetailInterface } from '../../models/cookbook-detail-interface';
import { UserService } from '../../services/user-service/user.service';
import { UserSummary } from '../../models/user-summary';
import { ShareRecipient } from '../../models/share-recipient';
import { MatAutocompleteModule } from '@angular/material/autocomplete';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { UpdateUserPermission } from '../../models/update-user-permission';
import { environment } from '../../../../environments/environment';

@Component({
  selector: 'app-cookbook-share-dialog',
  imports: [
    MatIcon,
    MatListModule,
    MatFormField,
    ReactiveFormsModule,
    MatButtonModule,
    MatOption,
    MatTableModule,
    MatAutocompleteModule,
    MatSelectModule,
    MatCheckboxModule,
    MatDialogActions,
    MatDialogContent,
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
  imageBaseUrl = environment.imageBaseUrl;

  sharedUsers: SharedUser[] = [];

  userAccessUpdateRequest: UpdateUserPermission[] = [];

  form = this.fb.group({
    username: [''],
    email: [''],
  });

  onClose(): void {
    this.dialogRef.close();
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
        this.userAccessUpdateRequest = this.sharedUsers.map((user) => ({
          userId: user.userId,
          permission: user.permission,
          avatarUrl: user.avatarUrl,
          removed: false,
        }));
      });
  }

  updateUserAccess(user: SharedUser): void {
    const update = this.userAccessUpdateRequest.find(
      (u) => u.userId === user.userId,
    );

    if (update) {
      update.permission = user.permission;
    }
  }

  removeUser(user: SharedUser, removed: boolean): void {
    const update = this.userAccessUpdateRequest.find(
      (u) => u.userId === user.userId,
    );

    if (update) {
      update.removed = removed;
    }
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

  saveChanges(): void {
    this.cookbookService
      .updateAccess(this.data.id, this.userAccessUpdateRequest)
      .subscribe({
        next: () => {
          this.dialogRef.close();
          this.snackbar.open('Your request(s) have been sent!', 'Dismiss', {
            duration: 5000,
          });
        },
      });
  }
}
