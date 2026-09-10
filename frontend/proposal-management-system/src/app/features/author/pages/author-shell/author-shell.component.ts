import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { APP_ICONS } from '../../../../core/icons/app-icons';
import { Navbar } from '../../../../shared/components/navbar/navbar.component';
import { SidebarComponent } from '../../../../shared/components/sidebar/sidebar.component';
import { NavItem } from '../../../../shared/models/nav-item.model';

@Component({
    selector: 'app-author-shell',
    imports: [RouterOutlet, SidebarComponent, Navbar],
    templateUrl: './author-shell.component.html',
    styleUrl: './author-shell.component.scss',
})
export class AuthorShellComponent {
    protected readonly navItems: NavItem[] = [
        {
            label: 'Proposals',
            route: '/app/author',
            icon: APP_ICONS.contract,
        },
    ];
}