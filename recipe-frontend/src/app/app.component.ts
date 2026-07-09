import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { HeaderComponent } from './features/header/header-component/header';
import { LoadingComponent } from './shared/loading/loading';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, HeaderComponent, LoadingComponent],
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent {
  title = 'recipe-frontend';
}
