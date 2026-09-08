import { Routes } from '@angular/router';
import { LandingPageComponent } from './features/landing/landing-page/landing-page.component';
import { LoginPage } from './features/auth/pages/login-page/login-page.component';
import { ProtectedLayout } from './features/protected/protected-layout.component';
import { AdminPage } from './features/protected/pages/admin-page/admin-page.component';
import { ReviewerPage } from './features/protected/pages/reviewer-page/reviewer-page.component';
import { AuthorPage } from './features/protected/pages/author-page/author-page.component';
import { roleGuard } from './core/guards/role.guard';

export const routes: Routes = [
  // 1. Landing Page strictly matches only the exact root URL
  { 
    path: '', 
    component: LandingPageComponent, 
    pathMatch: 'full' 
  },

  // 2. Authentication entry
  { 
    path: 'login', 
    component: LoginPage 
  },

  // 3. Protected Workspace Layout
  {
    path: 'app', // Or keep path: '' as a wrapper without colliding
    component: ProtectedLayout,
    children: [
      { 
        path: 'admin', 
        component: AdminPage, 
        canActivate: [roleGuard], 
        data: { role: 'ADMIN' } 
      },
      { 
        path: 'reviewer', 
        component: ReviewerPage, 
        canActivate: [roleGuard], 
        data: { role: 'REVIEWER' } 
      },
      { 
        path: 'author', 
        component: AuthorPage, 
        canActivate: [roleGuard], 
        data: { role: 'AUTHOR' } 
      },
    ],
  },

  // 4. Wildcard fallback
  { 
    path: '**', 
    redirectTo: '' 
  },
];