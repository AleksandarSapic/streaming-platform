import {Routes} from '@angular/router';
import {authGuard, guestGuard} from './guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    redirectTo: '/login',
    pathMatch: 'full'
  },
  {
    path: 'login',
    loadComponent: () => import('./components/login/login').then(c => c.Login),
    canActivate: [guestGuard]
  },
  {
    path: 'register',
    loadComponent: () => import('./components/register/register').then(c => c.Register),
    canActivate: [guestGuard]
  },
  {
    path: 'browse',
    loadComponent: () => import('./components/browse/browse').then(c => c.Browse),
    canActivate: [authGuard]
  },
  {
    path: 'movies',
    loadComponent: () => import('./components/movies/movies').then(c => c.Movies),
    canActivate: [authGuard]
  },
  {
    path: 'series',
    loadComponent: () => import('./components/browse/browse').then(c => c.Browse),
    canActivate: [authGuard]
  },
  {
    path: 'my-list',
    loadComponent: () => import('./components/browse/browse').then(c => c.Browse),
    canActivate: [authGuard]
  },
  {
    path: 'account',
    loadComponent: () => import('./components/account-profile/account-profile').then(c => c.AccountProfile),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
