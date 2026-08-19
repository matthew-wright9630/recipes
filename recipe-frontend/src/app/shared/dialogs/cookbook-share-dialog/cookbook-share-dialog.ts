import { Component, inject } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule } from '@angular/forms';
import { MatButtonModule } from '@angular/material/button';
import { MatIcon } from '@angular/material/icon';
import {
  MatError,
  MatFormField,
  MatInput,
  MatLabel,
} from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { RecipeService } from '../../services/recipe-service/recipe.service';
import { CookbookService } from '../../services/cookbook-service/cookbook.service';
import { CookbookStateService } from '../../services/cookbook-state/cookbook-state.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { MatSnackBar } from '@angular/material/snack-bar';
import { MatOption, MatSelect } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { SharedUser } from '../../models/shared-user';
import { User } from '../../models/user';
import { CookbookDetailInterface } from '../../models/cookbook-detail-interface';

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
          accessLevel: user.accessLevel,
        }));
      },
    });
  }

  addUser(user: User): void {
    if (this.sharedUsers.some((u) => u.userId === user.id)) {
      return;
    }

    this.sharedUsers.push({
      userId: user.id,
      username: user.username,
      email: user.email,
      accessLevel: 'READER',
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
