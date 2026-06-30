import { Component, inject, signal } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { MatInputModule } from '@angular/material/input';
import { MatFormFieldModule } from '@angular/material/form-field';
import { MatIconModule } from '@angular/material/icon';
import { HeaderService } from '../header.service';
import { MatDialog } from '@angular/material/dialog';
import { MatMenuModule } from '@angular/material/menu';
import { LoginDialogComponent } from '../../../shared/dialogs/login-dialog/login-dialog';
import { AuthStateService } from '../../../shared/services/auth-state-service/auth-state.service';
import { RegistrationDialog } from '../../../shared/dialogs/registration-dialog/registration-dialog';

@Component({
  selector: 'app-header-component',
  imports: [
    RouterLink,
    MatToolbarModule,
    MatButtonModule,
    MatInputModule,
    MatFormFieldModule,
    MatIconModule,
    MatMenuModule,
  ],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {
  constructor(
    private headerService: HeaderService,
    private dialog: MatDialog,
    private router: Router,
  ) {}

  authState = inject(AuthStateService);

  openLogin() {
    this.dialog.open(LoginDialogComponent, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  openRegistration(): void {
    this.dialog.open(RegistrationDialog, {
      width: '800px',
      maxWidth: '95vw',
      autoFocus: false,
    });
  }

  // employeeUser = signal(false);

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

  profileClick() {
    this.router.navigate(['/profile']);
  }
}
