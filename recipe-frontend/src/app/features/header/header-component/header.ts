import { Component, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { HeaderService } from '../header.service';
import { MatDialog } from '@angular/material/dialog';
import { LoginDialogComponent } from '../../../shared/dialogs/login-dialog/login-dialog';
import { AuthStateService } from '../../../shared/services/auth-state.service';

@Component({
  selector: 'app-header-component',
  imports: [
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {
  constructor(
    private headerService: HeaderService,
    private dialog: MatDialog,
  ) {
    this.getLoginDetails();
  }

  authState = inject(AuthStateService);

  openLogin() {
    this.dialog.open(LoginDialogComponent, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  getLoginDetails() {
    // this.authState.restoreSession();
    // return this.authState.currentUser();
    // this.httpService.getUserInfo().subscribe((data) => {
    //   this.dataPassService.loggedInUser.set(data);
    //   this.checkRoleType();
    // });
  }

  getLoggedInUser() {
    return this.authState.currentUser();
  }

  logout() {
    this.authState.logout();
    // this.httpService.logout();
    // this.dataPassService.loggedInUser.set(null);
  }

  // Check if the user is an employee or a guest.
  employeeUser = signal(false);

  checkRoleType() {
    //   if (
    //     this.dataPassService.loggedInUser()?.role === 'manager' ||
    //     this.dataPassService.loggedInUser()?.role === 'admin'
    //   ) {
    //     this.employeeUser.set(true);
    //   } else {
    //     this.employeeUser.set(false);
    //   }
  }
}
