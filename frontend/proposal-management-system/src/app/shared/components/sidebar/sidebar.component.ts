import { Component, Input, Output, EventEmitter } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';
import { NavItem } from '../../models/nav-item.model';
import { APP_ICONS } from '../../../core/icons/app-icons';

@Component({
  imports: [CommonModule, RouterModule, FontAwesomeModule],
  selector: 'app-sidebar',
  standalone: true,
  styleUrl: './sidebar.component.scss',
  templateUrl: './sidebar.component.html',
})
export class SidebarComponent {
  protected readonly icons = APP_ICONS;

  @Input ({required: true}) navItems: NavItem[] = [];
  @Output() logout = new EventEmitter<void>();

  isCollapsed = false;
  toggleSidebar(): void {
    this.isCollapsed = !this.isCollapsed;
  }

  onLogout(): void {
    this.logout.emit();
  }
}
