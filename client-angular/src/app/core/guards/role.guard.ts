import { Injectable, inject } from '@angular/core';
import { CanActivateFn, Router, ActivatedRouteSnapshot, RouterStateSnapshot } from '@angular/router';
import { AuthService } from '../services/auth.service';

@Injectable({
  providedIn: 'root'
})
export class RoleGuardService {
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const requiredRoles = route.data['roles'] as Array<string>;
    const userRole = this.authService.getUserRole();

    console.log('🔐 RoleGuard checking - Required:', requiredRoles, 'User role:', userRole);

    if (!userRole) {
      console.log('❌ No role found, redirecting to login');
      this.router.navigate(['/auth/login']);
      return false;
    }

    if (requiredRoles && requiredRoles.length > 0) {
      if (!requiredRoles.includes(userRole)) {
        console.log('❌ User role not in required roles, redirecting to unauthorized');
        this.router.navigate(['/unauthorized']);
        return false;
      }
    }

    console.log('✅ RoleGuard passed');
    return true;
  }
}

export const roleGuard: CanActivateFn = (route, state) => {
  return inject(RoleGuardService).canActivate(route, state);
};
