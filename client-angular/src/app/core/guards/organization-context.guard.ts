import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { OrganizationService } from '../services/organization.service';

export const organizationContextGuard: CanActivateFn = () => {
  const organizationService = inject(OrganizationService);
  const router = inject(Router);

  if (organizationService.getActiveOrganizationIdSnapshot()) {
    return true;
  }

  router.navigate(['/organizations']);
  return false;
};
