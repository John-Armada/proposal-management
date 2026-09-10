import { Routes } from '@angular/router';
import { authGuard, publicOnlyGuard, roleGuard } from './core/guards/auth.guard';

export const routes: Routes = [
  {
    path: '',
    canActivate: [publicOnlyGuard],
    pathMatch: 'full',
    loadComponent: () =>
      import('./features/landing/pages/landing-page/landing-page.component')
        .then((module) => module.LandingPageComponent),
  },
  {
    path: 'login',
    canActivate: [publicOnlyGuard],
    loadComponent: () =>
      import('./features/auth/pages/login-page/login-page.component')
        .then((module) => module.LoginPage),
  },
  {
    path: 'app/admin',
    canActivate: [authGuard, roleGuard(['ADMIN'])],
    loadComponent: () =>
      import('./features/admin/pages/user-management/user-management.component')
        .then((module) => module.UserManagementComponent),
  },
  {
    path: 'app/author',
    canActivate: [authGuard, roleGuard(['AUTHOR'])],
    loadChildren: () =>
      import('./features/author/proposal.routes').then(m => m.PROPOSAL_ROUTES),
  },
  {
    path: '**',
    redirectTo: 'login',
  },
];