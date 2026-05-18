import { Component, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from '@angular/material/button';
import { HeaderService } from '../header.service';

@Component({
  selector: 'app-header-component',
  imports: [RouterLink, MatToolbarModule, MatButtonModule],
  templateUrl: './header.html',
  styleUrl: './header.scss',
})
export class HeaderComponent {
  constructor(
    private headerService: HeaderService,
  ) {
    this.getLoginDetails();
  }

  login() {
    // window.location.href = '/oauth2/authorization/google';
  }

  getLoginDetails() {
    // this.httpService.getUserInfo().subscribe((data) => {
    //   this.dataPassService.loggedInUser.set(data);
    //   this.checkRoleType();
    // });
  }

  getLoggedInUser() {
    // return this.dataPassService?.loggedInUser();
  }

  logout() {
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