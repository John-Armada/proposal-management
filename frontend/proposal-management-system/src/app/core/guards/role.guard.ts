import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AuthService } from '../services/auth.service';
import { AppUserRole } from '../../shared/models/auth-session.model';

export const roleGuard: CanActivateFn = (_route, state) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const session = auth.session();

  if (!session || !auth.isAuthenticated()) {
    return router.createUrlTree(['/'], {
      queryParams: { returnUrl: state.url },
    });
  }

  const requiredRole = _route.data['role'] as AppUserRole;
  if (session.role === requiredRole) {
    return true;
  }

  return router.createUrlTree([routeForRole(session.role)]);
};

function routeForRole(role: AppUserRole): string {
  switch (role) {
    case 'ADMIN':
      return '/admin';
    case 'REVIEWER':
      return '/reviewer';
    case 'AUTHOR':
      return '/author';
  }
}
