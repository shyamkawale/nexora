import { Injectable } from '@angular/core';
import { Observable, BehaviorSubject } from 'rxjs';
import { HttpService } from './http.service';

export interface SearchUserResponse {
  publicId: string;
  username: string;
  email: string;
  profilePicture?: string;
  bio?: string;
}

@Injectable({
  providedIn: 'root'
})
export class PeopleService {
  private usersCache$ = new BehaviorSubject<SearchUserResponse[]>([]);

  constructor(private httpService: HttpService) {}

  getAllUsers(page: number = 0, size: number = 20): Observable<any> {
    const url = `/api/v1/users?page=${page}&size=${size}`;
    console.log('📡 Fetching all users from:', url);
    return this.httpService.get(url);
  }

  searchUsers(query: string): Observable<SearchUserResponse[]> {
    const url = `/api/v1/users/search?q=${query}`;
    console.log('🔍 Searching users:', url);
    return this.httpService.get<SearchUserResponse[]>(url);
  }

  getCachedUsers(): Observable<SearchUserResponse[]> {
    return this.usersCache$.asObservable();
  }

  setCachedUsers(users: SearchUserResponse[]): void {
    this.usersCache$.next(users);
  }
}
