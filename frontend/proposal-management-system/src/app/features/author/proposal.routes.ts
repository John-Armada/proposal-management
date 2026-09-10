import { Routes } from '@angular/router';
import { AuthorShellComponent } from './pages/author-shell/author-shell.component';

/**
 * Register under the app's root routes, e.g.:
 *   { path: 'proposals', loadChildren: () => import('./features/proposals/proposals.routes').then(m => m.PROPOSAL_ROUTES) }
 */
export const PROPOSAL_ROUTES: Routes = [
    {
        path: '',
        component: AuthorShellComponent,
        children: [
            {
                path: '',
                loadComponent: () =>
                    import('./pages/proposal-list-page/proposal-list-page.component').then(
                        (m) => m.ProposalListPageComponent,
                    ),
                title: 'Proposals',
            },
            {
                path: 'new',
                loadComponent: () =>
                    import('./pages/proposal-form-page/proposal-form-page.component').then(
                        (m) => m.ProposalFormPageComponent,
                    ),
                title: 'New proposal',
            },
            {
                path: ':id/edit',
                loadComponent: () =>
                    import('./pages/proposal-form-page/proposal-form-page.component').then(
                        (m) => m.ProposalFormPageComponent,
                    ),
                title: 'Edit proposal',
            },
            {
                path: ':id',
                loadComponent: () =>
                    import('./pages/proposal-detail-page/proposal-detail-page.component').then(
                        (m) => m.ProposalDetailPageComponent,
                    ),
                title: 'Proposal',
            },
        ],
    },
];