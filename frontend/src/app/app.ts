import {Component, signal} from '@angular/core';
import {RouterOutlet, Router} from '@angular/router';
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

  constructor(private authService: AuthService, private router: Router) {
  }

  get isAuthenticated() { return this.authService.isAuthenticated(); }
  
  get isWatchRoute() { return this.router.url.startsWith('/watch/'); }
}
