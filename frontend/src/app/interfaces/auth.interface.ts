import {User} from './user.interface';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  fullName: string;
  email: string;
  password: string;
  country: string;
}

export interface AuthResponse {
  token: string;
  type: string;
  user: User;
}
