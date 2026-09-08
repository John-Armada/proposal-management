import { Routes } from '@angular/router';
import { LandingPageComponent } from './features/landing/landing-page/landing-page.component';

import { roleGuard } from './core/guards/role.guard';
import { LoginPage } from './features/auth/pages/login-page/login-page.component';
import { AdminPage } from './features/protected/pages/admin-page/admin-page.component';
import { ReviewerPage } from './features/protected/pages/reviewer-page/reviewer-page.component';
import { AuthorPage } from './features/protected/pages/author-page/author-page.component';
import { ProtectedLayout } from './features/protected/protected-layout.component';

export const routes: Routes = [
	{ path: '', component: LoginPage },
	{ path: 'login', component: LoginPage },
	{
		path: '',
		component: ProtectedLayout,
		children: [
			{ path: 'admin', component: AdminPage, canActivate: [roleGuard], data: { role: 'ADMIN' } },
			{ path: 'reviewer', component: ReviewerPage, canActivate: [roleGuard], data: { role: 'REVIEWER' } },
			{ path: 'author', component: AuthorPage, canActivate: [roleGuard], data: { role: 'AUTHOR' } },
		],
	},
	{ path: '**', redirectTo: '' },
];
