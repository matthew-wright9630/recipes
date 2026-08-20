import { Component, inject } from '@angular/core';
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
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { SharedUser } from '../../models/shared-user';
import { CookbookDetailInterface } from '../../models/cookbook-detail-interface';
import { UserService } from '../../services/user-service/user.service';

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
    MatSelect,
    MatOption,
    MatTableModule,
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
  private snackbar = inject(MatSnackBar);
  data = inject<CookbookDetailInterface>(MAT_DIALOG_DATA);

  displayedColumns = ['user', 'access', 'remove'];
  sharedUsers: SharedUser[] = [];
  searchControl = new FormControl('');

  form = this.fb.group({
    username: [''],
    email: [''],
  });

  onClose(): void {
    this.dialogRef.close();
  }

  ngOnInit(): void {
    this.loadSharedUsers();
  }

  loadSharedUsers(): void {
    this.cookbookService.getSharedUsers(this.data.id).subscribe({
      next: (users: SharedUser[]) => {
        this.sharedUsers = users.map((user) => ({
          ...user,
          permission: user.permission,
        }));
      },
    });
    this.sharedUsers = [...this.sharedUsers].sort((a, b) => {
      if (a.permission === 'OWNER') return -1;
      if (b.permission === 'OWNER') return 1;
      return 0;
    });
  }

  addUser(): void {
    const email = this.form.value.email?.trim().toLowerCase();

    if (!email) {
      return;
    }

    this.userService.getSharedUserDetails(email).subscribe({
      next: (user: SharedUser) => {
        this.sharedUsers = [
          ...this.sharedUsers,
          {
            userId: user.userId,
            username: user.username,
            permission: 'READ',
          },
        ];
      },
      error: () => {
        // If someone inputs incorrect information, the value is still set. This is to prevent account-enumeration
        this.sharedUsers = [
          ...this.sharedUsers,
          {
            userId: 0,
            username: email,
            permission: 'READ',
          },
        ];
      },
    });
  }

  removeUser(user: SharedUser): void {
    this.sharedUsers = this.sharedUsers.filter((u) => u.userId !== user.userId);
  }

  saveChanges(): void {
    this.cookbookService
      .updateAccess(this.data.id, this.sharedUsers)
      .subscribe({
        next: () => {
          this.dialogRef.close(true);
        },
      });
  }
}
