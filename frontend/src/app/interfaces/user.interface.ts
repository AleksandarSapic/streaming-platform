export interface UserRole {
  id: string;
  name: string;
}

export interface User {
  id: string;
  fullName: string;
  email: string;
  country: string;
  createdAt: string;
  updatedAt: string;
  userRole: UserRole;
}