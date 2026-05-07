import { Injectable, Inject, PLATFORM_ID } from '@angular/core';
import {
  HttpClient,
  HttpHeaders,
  HttpErrorResponse,
  HttpRequest,
  HttpEvent
} from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';
import { isPlatformBrowser } from '@angular/common';

@Injectable({
  providedIn: 'root'
})
export class HttpService {
  private baseUrl = environment.serverUrl;
  private isBrowser: boolean;

  constructor(
    private http: HttpClient,
    @Inject(PLATFORM_ID) platformId: Object
  ) {
    this.isBrowser = isPlatformBrowser(platformId);
  }

  private getHeaders(): HttpHeaders {
    const token = this.getAuthToken();
    let headers = new HttpHeaders({
      'Content-Type': 'application/json'
    });

    if (token) {
      console.log('🔐 Token found, adding Authorization header');
      headers = headers.set('Authorization', `Bearer ${token}`);
    } else {
      console.warn('⚠️ No auth token found!');
    }

    return headers;
  }

  private getAuthToken(): string | null {
    if (!this.isBrowser) {
      console.log('⚠️ Not in browser context, no token available');
      return null;
    }
    const token = localStorage.getItem('authToken');
    if (!token) {
      console.warn('⚠️ No token in localStorage');
    }
    return token || null;
  }

  private handleError(error: HttpErrorResponse) {
    console.error('❌ HTTP Error:', {
      status: error.status,
      statusText: error.statusText,
      message: error.message,
      url: error.url,
      errorBody: error.error
    });

    if (error.status === 401) {
        console.log('🔄 401 received (not auth endpoint) — delegating handling to AuthInterceptor');
    } else if (error.status === 403) {
      console.error('🚫 Access Forbidden - check permissions or CORS');
    } else if (error.status === 307) {
      if (this.isBrowser) {
        window.location.href = error.headers.get('Location') || '/';
      }
    }

    return throwError(() => ({
      status: error.status,
      message: error.error?.message || error.message,
      error: error.error
    }));
  }

  get<T>(endpoint: string): Observable<T> {
    const url = `${this.baseUrl}${endpoint}`;
    const headers = this.getHeaders();
    console.log('📡 GET:', url, 'Headers:', headers.keys());
    return this.http.get<any>(url, { headers }).pipe(
      map((res: any) => (res && Object.prototype.hasOwnProperty.call(res, 'data') ? res.data : res)),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  post<T>(endpoint: string, data: any): Observable<T> {
    const url = `${this.baseUrl}${endpoint}`;
    const isAuthEndpoint = endpoint.startsWith('/api/v1/auth');
    const options: any = { headers: this.getHeaders() };
    if (isAuthEndpoint) {
      // Ensure cookies (HttpOnly refresh token) are accepted/set by browser for auth endpoints
      options.withCredentials = true;
    }

    return this.http.post<any>(url, data, options).pipe(
      map((res: any) => (res && Object.prototype.hasOwnProperty.call(res, 'data') ? res.data : res)),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  put<T>(endpoint: string, data: any): Observable<T> {
    return this.http.put<any>(
      `${this.baseUrl}${endpoint}`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      map((res: any) => (res && Object.prototype.hasOwnProperty.call(res, 'data') ? res.data : res)),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  patch<T>(endpoint: string, data: any): Observable<T> {
    return this.http.patch<any>(
      `${this.baseUrl}${endpoint}`,
      data,
      { headers: this.getHeaders() }
    ).pipe(
      map((res: any) => (res && Object.prototype.hasOwnProperty.call(res, 'data') ? res.data : res)),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  delete<T>(endpoint: string): Observable<T> {
    return this.http.delete<any>(
      `${this.baseUrl}${endpoint}`,
      { headers: this.getHeaders() }
    ).pipe(
      map((res: any) => (res && Object.prototype.hasOwnProperty.call(res, 'data') ? res.data : res)),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }

  postFormData<T>(endpoint: string, formData: FormData): Observable<T> {
    let headers = new HttpHeaders();

    if (this.isBrowser) {
      const token = localStorage.getItem('authToken');
      if (token) {
        headers = headers.set('Authorization', `Bearer ${token}`);
      }
    }

    return this.http.post<any>(
      `${this.baseUrl}${endpoint}`,
      formData,
      { headers }
    ).pipe(
      map((res: any) => res?.data),
      catchError((error: HttpErrorResponse) => this.handleError(error))
    );
  }
}
