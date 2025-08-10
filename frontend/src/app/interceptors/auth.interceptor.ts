import {HttpInterceptorFn} from '@angular/common/http';
import {inject} from '@angular/core';
import {AuthService} from '../services/auth.service';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getToken();

  // Don't add token to auth endpoints
  if (req.url.includes('/auth/')) {
    return next(req);
  }

  // Add token to requests if available and not expired
  if (token && !authService.isTokenExpired()) {
    const authReq = req.clone({
      headers: req.headers.set('Authorization', `Bearer ${token}`)
    });
    return next(authReq);
  }

  // If token is expired, logout user
  if (token && authService.isTokenExpired()) {
    authService.logout();
  }

  return next(req);
};
