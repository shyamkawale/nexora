import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { HttpService } from './http.service';

export interface User {
  publicId: string;
  email: string;
  username: string;
  profilePicture?: string;
  bio?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface LoginUserInfo {
  id: string;
  username: string;
  roles: string[];
}

export interface LoginApiResponse {
  accessToken: string;
  tokenType: string;
  expiresIn: number;
  user: LoginUserInfo;
}

export interface AuthResponse {
  token: string;
  user: User;
}

@Injectable({
  providedIn: 'root'
})
export class UserService {
  constructor(private httpService: HttpService) {}

  getUserInfo(): Observable<User> {
    return this.httpService.get<User>('/api/v1/user/info');
  }

  getUserProfile(userId: string): Observable<User> {
    return this.httpService.get<User>(`/api/v1/user/profile/${userId}`);
  }

  updateProfile(userData: Partial<User>): Observable<User> {
    return this.httpService.put<User>('/api/v1/user/info', userData);
  }

  uploadProfilePicture(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('file', file);
    return this.httpService.postFormData('/api/v1/user/profile-picture', formData);
  }
}
