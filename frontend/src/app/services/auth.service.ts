import {Injectable, signal} from '@angular/core';
import {HttpClient} from '@angular/common/http';
import {Observable, tap} from 'rxjs';
import {AuthResponse, LoginRequest, RegisterRequest} from '../interfaces/auth.interface';
import {User} from '../interfaces/user.interface';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly API_BASE_URL = 'http://localhost:8080/api/v1/auth';
  private readonly TOKEN_KEY = 'streaming_platform_token';

  private _currentUser = signal<User | null>(null);
  private _isAuthenticated = signal<boolean>(false);

  public readonly currentUser = this._currentUser.asReadonly();
  public readonly isAuthenticated = this._isAuthenticated.asReadonly();

  constructor(private http: HttpClient) {
    this.loadUserFromStorage();
  }

  login(loginRequest: LoginRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_BASE_URL}/login`, loginRequest)
      .pipe(
        tap(response => this.handleAuthSuccess(response))
      );
  }

  register(registerRequest: RegisterRequest): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.API_BASE_URL}/register`, registerRequest)
      .pipe(
        tap(response => this.handleAuthSuccess(response))
      );
  }

  checkEmailExists(email: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_BASE_URL}/check-email`, {
      params: {email}
    });
  }

  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem('streaming_platform_user');
    this._currentUser.set(null);
    this._isAuthenticated.set(false);
  }

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (!token) return true;

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      const exp = payload.exp * 1000; // Convert to milliseconds
      return Date.now() >= exp;
    } catch (error) {
      return true;
    }
  }

  private handleAuthSuccess(response: AuthResponse): void {
    localStorage.setItem(this.TOKEN_KEY, response.token);
    localStorage.setItem('streaming_platform_user', JSON.stringify(response.user));
    this._currentUser.set(response.user);
    this._isAuthenticated.set(true);
  }

  private loadUserFromStorage(): void {
    const token = this.getToken();
    const userJson = localStorage.getItem('streaming_platform_user');

    if (token && userJson && !this.isTokenExpired()) {
      try {
        const user = JSON.parse(userJson);
        this._currentUser.set(user);
        this._isAuthenticated.set(true);
      } catch (error) {
        this.logout();
      }
    } else {
      this.logout();
    }
  }
}
