import { Injectable, Injector } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent,
  HttpErrorResponse,
  HttpClient,
  HttpBackend
} from '@angular/common/http';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { catchError, filter, switchMap, take, finalize } from 'rxjs/operators';
import { Router } from '@angular/router';
import { environment } from '../../../environments/environment';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private isRefreshing = false;
  private refreshTokenSubject: BehaviorSubject<string | null> = new BehaviorSubject<string | null>(null);
  private httpClient: HttpClient;

  constructor(private injector: Injector, private httpBackend: HttpBackend, private router: Router) {
    // create HttpClient that bypasses the interceptor chain to call refresh endpoint
    this.httpClient = new HttpClient(this.httpBackend);
  }

  intercept(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.debug('[AuthInterceptor] intercept ->', request.method, request.url);
    const token = localStorage.getItem('authToken');
    let authReq = request;

    if (token) {
      console.debug('[AuthInterceptor] attaching token for', request.url);
      authReq = request.clone({
        setHeaders: {
          Authorization: `Bearer ${token}`
        }
      });
    }

    return next.handle(authReq).pipe(
      catchError((error: HttpErrorResponse) => {
        console.debug('[AuthInterceptor] caught error ->', error.status, request.url);
        if (error.status === 401 && !this.isRefreshOrAuthRequest(request)) {
          return this.handle401Error(authReq, next);
        }
        return throwError(() => error);
      })
    );
  }

  private isRefreshOrAuthRequest(req: HttpRequest<any>): boolean {
    const url = req.url;
    return url.includes('/api/v1/auth/refresh-token') || url.includes('/api/v1/auth/login') || url.includes('/api/v1/auth/signup') || url.includes('/api/v1/auth/logout');
  }

  private handle401Error(request: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    console.debug('[AuthInterceptor] handle401Error for', request.url);
    if (!this.isRefreshing) {
      this.isRefreshing = true;
      this.refreshTokenSubject.next(null);

      const refreshUrl = `${environment.serverUrl}/api/v1/auth/refresh-token`;
      console.debug('[AuthInterceptor] calling refresh endpoint', refreshUrl);

return this.httpClient.get(refreshUrl, { observe: 'response', withCredentials: true }).pipe(
         switchMap(response => {
           console.debug('[AuthInterceptor] refresh response headers all keys:', Array.from(response.headers.keys()));
           console.debug('[AuthInterceptor] refresh response Authorization (direct):', response.headers.get('Authorization'));
           console.debug('[AuthInterceptor] refresh response authorization (lower):', response.headers.get('authorization'));
           let authHeader: string | null = response.headers.get('authorization') || response.headers.get('Authorization');
           if (!authHeader) {
             for (const key of response.headers.keys()) {
               if (key.toLowerCase() === 'authorization') {
                 authHeader = response.headers.get(key);
                 break;
               }
             }
           }
           console.debug('[AuthInterceptor] refresh response Authorization header =', authHeader);
          if (authHeader) {
            const newToken = authHeader.replace(/^Bearer\s+/i, '');
            console.debug('[AuthInterceptor] refresh succeeded, storing new token');
            localStorage.setItem('authToken', newToken);

            // notify queued requests
            this.refreshTokenSubject.next(newToken);
            return next.handle(this.addTokenHeader(request, newToken));
          }

          // refresh failed: clear auth and redirect
          console.debug('[AuthInterceptor] refresh failed (no Authorization header)');
          this.clearLocalAuthAndRedirect();
          return throwError(() => new Error('Refresh token failed'));
        }),
        catchError((err) => {
          console.debug('[AuthInterceptor] refresh call errored', err);
          this.clearLocalAuthAndRedirect();
          return throwError(() => err);
        }),
        finalize(() => {
          console.debug('[AuthInterceptor] refresh finalize');
          this.isRefreshing = false;
        })
      );
    }

    // If refresh already in progress, wait for it to finish
    console.debug('[AuthInterceptor] refresh already in progress, queuing request');
    return this.refreshTokenSubject.pipe(
      filter(token => token != null),
      take(1),
      switchMap((token) => {
        console.debug('[AuthInterceptor] retrying queued request with new token');
        return next.handle(this.addTokenHeader(request, token as string));
      })
    );
  }

  private addTokenHeader(request: HttpRequest<any>, token: string): HttpRequest<any> {
    return request.clone({ setHeaders: { Authorization: `Bearer ${token}` } });
  }

  private clearLocalAuthAndRedirect(): void {
    localStorage.removeItem('authToken');
    localStorage.removeItem('tokenType');
    localStorage.removeItem('expiresIn');
    localStorage.removeItem('currentUser');
    localStorage.removeItem('userRoles');
    this.router.navigate(['/auth/signin']);
  }
}
