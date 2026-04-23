import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import { BehaviorSubject, Observable } from 'rxjs';
import { HttpService } from './http.service';
import { StateService } from './state.service';
import { map } from 'rxjs/operators';
import { User, AuthResponse, LoginApiResponse } from './user.service';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private isAuthenticatedSubject = new BehaviorSubject<boolean>(false);
  public isAuthenticated$ = this.isAuthenticatedSubject.asObservable();

  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  private isInitializingSubject = new BehaviorSubject<boolean>(true);
  public isInitializing$ = this.isInitializingSubject.asObservable();

  private isBrowser: boolean;

  constructor(
    private httpService: HttpService,
    private stateService: StateService,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
    // Initialize auth state on app startup
    this.initializeAuthState();
  }

  /**
   * Initialize authentication state from localStorage on app startup
   */
  private initializeAuthState(): void {
    if (!this.isBrowser) {
      console.log('⚠️ Not in browser environment, skipping auth init');
      this.isInitializingSubject.next(false);
      return;
    }
    
    console.log('🔍 Initializing auth state from localStorage...');
    const token = localStorage.getItem('authToken');
    const userStr = localStorage.getItem('currentUser');
    
    console.log('📦 Token exists:', !!token);
    console.log('📦 User exists:', !!userStr);
    
    if (token && userStr) {
      try {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
        this.isAuthenticatedSubject.next(true);
        console.log('✅ Auth state restored successfully for user:', user?.username || user?.email);
      } catch (error) {
        console.error('❌ Error parsing stored user:', error);
        this.clearAuth();
      }
    } else {
      console.log('⚠️ No token or user found in localStorage - user is logged out');
      this.isAuthenticatedSubject.next(false);
    }
    
    // Mark initialization as complete
    this.isInitializingSubject.next(false);
    console.log('✅ Auth initialization complete');
  }

  signin(email: string, password: string): Observable<LoginApiResponse> {
    return this.httpService.post<LoginApiResponse>('/api/v1/auth/login', { email, password }).pipe(
      map(response => this.handleLoginResponse(response))
    );
  }

  signup(userData: any): Observable<any> {
    return this.httpService.post<any>('/api/v1/auth/signup', userData).pipe(
      map(response => response) // Just return the response (usually 201 with no body)
    );
  }

  private handleLoginResponse(response: LoginApiResponse): LoginApiResponse {
    if (!this.isBrowser) return response;

    // Store the access token
    if (response.accessToken) {
      localStorage.setItem('authToken', response.accessToken);
      localStorage.setItem('tokenType', response.tokenType);
      localStorage.setItem('expiresIn', response.expiresIn.toString());
    }

    // Store user info
    if (response.user) {
      const userInfo = {
        id: response.user.id,
        username: response.user.username,
        roles: response.user.roles
      };
      localStorage.setItem('currentUser', JSON.stringify(userInfo));
      localStorage.setItem('userRoles', JSON.stringify(response.user.roles));
      this.currentUserSubject.next(userInfo as any);
      this.stateService.setMyProfile(userInfo as any);
    }

    this.isAuthenticatedSubject.next(true);
    this.stateService.setAuthenticated(true);

    return response;
  }

  logout(): Observable<any> {
    return this.httpService.post('/api/v1/auth/logout', {}).pipe(
      map(() => {
        this.clearAuth();
        return true;
      })
    );
  }

  private clearAuth(): void {
    if (!this.isBrowser) return;

    localStorage.removeItem('authToken');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('expiresIn');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRoles');
    localStorage.removeItem('appState');
    this.isAuthenticatedSubject.next(false);
    this.currentUserSubject.next(null);
    this.stateService.resetState();
  }

  clearAuthLocally(): void {
    // Public method to clear auth if API call fails
    this.clearAuth();
  }

  refreshToken(): Observable<AuthResponse> {
    return this.httpService.post<AuthResponse>('/api/v1/auth/refresh', {}).pipe(
      map(response => {
        if (this.isBrowser && response.token) {
          localStorage.setItem('authToken', response.token);
        }
        return response;
      })
    );
  }

  isAuthenticated(): boolean {
    return this.isAuthenticatedSubject.value;
  }

  getIsInitializing(): boolean {
    return this.isInitializingSubject.value;
  }

  getCurrentUserSync(): User | null {
    return this.currentUserSubject.value;
  }

  /**
   * Get user role from stored roles array (returns first role, without ROLE_ prefix)
   */
  getUserRole(): string | null {
    if (!this.isBrowser) return null;

    try {
      // First try to get role from stored roles array (new login response format)
      const rolesStr = localStorage.getItem('userRoles');
      if (rolesStr) {
        const roles: string[] = JSON.parse(rolesStr);
        if (roles && roles.length > 0) {
          const role = roles[0];
          // Remove 'ROLE_' prefix if present (e.g., "ROLE_ADMIN" -> "ADMIN")
          if (role && role.startsWith('ROLE_')) {
            return role.substring(5);
          }
          return role;
        }
      }

      // Fallback: try to extract role from JWT token
      const token = localStorage.getItem('authToken');
      if (!token) return null;

      const decodedToken = this.decodeToken(token);
      const roleWithPrefix = decodedToken?.role || null;
      
      // Remove 'ROLE_' prefix if present (e.g., "ROLE_ADMIN" -> "ADMIN")
      if (roleWithPrefix && roleWithPrefix.startsWith('ROLE_')) {
        return roleWithPrefix.substring(5);
      }
      return roleWithPrefix;
    } catch (error) {
      console.error('❌ Error extracting role:', error);
      return null;
    }
  }

  /**
   * Check if user has a specific role (checks without ROLE_ prefix)
   */
  hasRole(role: string): boolean {
    return this.getUserRole() === role;
  }

  /**
   * Check if user is admin
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * Decode JWT token (client-side only for reading claims)
   */
  private decodeToken(token: string): any {
    try {
      const base64Url = token.split('.')[1];
      const base64 = base64Url.replace(/-/g, '+').replace(/_/g, '/');
      const jsonPayload = decodeURIComponent(
        atob(base64)
          .split('')
          .map((c) => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
          .join('')
      );
      return JSON.parse(jsonPayload);
    } catch (error) {
      console.error('❌ Error decoding token:', error);
      return null;
    }
  }

  /**
   * Public method to recheck auth state (useful for debugging or manual refresh)
   */
  recheckAuthState(): void {
    console.log('🔄 Rechecking auth state...');
    this.initializeAuthState();
  }
}
