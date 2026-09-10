import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';

import { AppUserRole } from '../../shared/models/auth-session.model';
import { AuthService } from '../services/auth.service';

/** Blocks access unless the user has a valid, non-expired session. */
export const authGuard: CanActivateFn = (_route, state) => {
  const authService = inject(AuthService);
  const router = inject(Router);

  if (authService.isAuthenticated()) {
    return true;
  }

  return router.createUrlTree(['/login'], {
    queryParams: { returnUrl: state.url },
  });
};

/** Keeps logged-in users out of public-only routes. */
export const publicOnlyGuard: CanActivateFn = () => {
  const authService = inject(AuthService);
  const router = inject(Router);
  const session = authService.session();

  if (!authService.isAuthenticated() || !session) {
    return true;
  }

  const targetRoute = authService.resolveLandingRoute(session);
  return targetRoute === '/login'
    ? true
    : router.createUrlTree([targetRoute]);
};

/** Restricts a route to a specific set of roles. */
export function roleGuard(allowedRoles: AppUserRole[]): CanActivateFn {
  return () => {
    const authService = inject(AuthService);
    const router = inject(Router);
    const session = authService.session();

    if (!authService.isAuthenticated()) {
      return router.createUrlTree(['/login']);
    }

    if (!session) {
      return router.createUrlTree(['/login']);
    }

    if (allowedRoles.includes(session.role)) {
      return true;
    }

    const fallbackRoute = authService.resolveLandingRoute(session);
    return router.createUrlTree([fallbackRoute]);
  };
}
