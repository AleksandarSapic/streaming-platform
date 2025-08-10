import {Component, signal} from '@angular/core';
import {RouterOutlet} from '@angular/router';
import {Header} from './components/header/header';
import {AuthService} from './services/auth.service';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, Header],
  templateUrl: './app.html',
  styleUrl: './app.css'
})
export class App {
  protected readonly title = signal('Streaming platform');

  constructor(private authService: AuthService) {
  }

  get isAuthenticated() { return this.authService.isAuthenticated(); }
}
