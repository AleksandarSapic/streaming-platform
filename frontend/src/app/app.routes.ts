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
    loadComponent: () => import('./components/series/series').then(c => c.Series),
    canActivate: [authGuard]
  },
  {
    path: 'my-list',
    loadComponent: () => import('./components/user-list/user-list').then(c => c.UserList),
    canActivate: [authGuard]
  },
  {
    path: 'account',
    loadComponent: () => import('./components/account-profile/account-profile').then(c => c.AccountProfile),
    canActivate: [authGuard]
  },
  {
    path: 'search',
    loadComponent: () => import('./components/search/search').then(c => c.Search),
    canActivate: [authGuard]
  },
  {
    path: 'watch/:id',
    loadComponent: () => import('./components/video-player/video-player').then(c => c.VideoPlayer),
    canActivate: [authGuard]
  },
  {
    path: 'content/:id',
    loadComponent: () => import('./components/content-detail/content-detail').then(c => c.ContentDetail),
    canActivate: [authGuard]
  },
  {
    path: 'show/:id',
    loadComponent: () => import('./components/show-detail/show-detail').then(c => c.ShowDetail),
    canActivate: [authGuard]
  },
  {
    path: '**',
    redirectTo: '/login'
  }
];
