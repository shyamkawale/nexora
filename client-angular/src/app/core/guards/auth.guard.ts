import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';
import { Observable } from 'rxjs';
import { map, filter, take, toArray } from 'rxjs/operators';

@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(
    route: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): Observable<boolean> | Promise<boolean> | boolean {
    const isInitializing = this.authService.getIsInitializing();
    
    console.log('🔐 AuthGuard - Route:', state.url, 'isInitializing:', isInitializing);
    
    // If already initialized, check synchronously
    if (!isInitializing) {
      const isAuthenticated = this.authService.isAuthenticated();
      console.log('✅ Already initialized - isAuthenticated:', isAuthenticated);
      
      if (!isAuthenticated) {
        this.router.navigate(['/auth/signin']);
      }
      return isAuthenticated;
    }
    
    // If still initializing, wait for it to complete
    console.log('⏳ Still initializing, waiting...');
    return this.authService.isInitializing$.pipe(
      filter(val => !val),  // Wait for false
      take(1),
      map(() => {
        const isAuthenticated = this.authService.isAuthenticated();
        console.log('✅ After wait - isAuthenticated:', isAuthenticated);
        
        if (!isAuthenticated) {
          this.router.navigate(['/auth/signin']);
        }
        return isAuthenticated;
      })
    );
  }
}

