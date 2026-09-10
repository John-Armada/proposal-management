import { Component, input, signal } from '@angular/core';
import { NgOptimizedImage } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NavItem } from '../../models/nav-item.model';
import { APP_ICONS } from '../../../core/icons/app-icons';

@Component({
  imports: [NgOptimizedImage, RouterModule, FontAwesomeModule],
  selector: 'app-sidebar',
  styleUrl: './sidebar.component.scss',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  protected readonly icons = APP_ICONS;

  readonly navItems = input.required<NavItem[]>();

  readonly isCollapsed = signal(false);

  toggleSidebar(): void {
    this.isCollapsed.update((collapsed) => !collapsed);
  }
}
