import { Component, computed, inject, input, output, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AuthService } from '../../../core/services/auth.service';
import { APP_ICONS } from '../../../core/icons/app-icons';
import { NotificationItem } from '../../models/notification.model';

@Component({
  imports: [FormsModule, FontAwesomeModule],
  selector: 'app-navbar',
  host: {
    '(document:keydown.escape)': 'onEscape()',
  },
  styleUrl: './navbar.component.scss',
  templateUrl: './navbar.component.html',
})
export class Navbar {
  private readonly auth = inject(AuthService);

  protected readonly icons = APP_ICONS;
  protected readonly session = this.auth.session;

  protected readonly displayName = computed(() => {
    const session = this.session();
    return session ? `${session.firstName} ${session.lastName}`.trim() : '';
  });

  protected readonly initials = computed(() => {
    const session = this.session();
    if (!session) {
      return '';
    }

    const fromName = `${session.firstName?.[0] ?? ''}${session.lastName?.[0] ?? ''}`.toUpperCase();
    return fromName || session.email[0]?.toUpperCase() || '?';
  });

  readonly notifications = input<NotificationItem[]>([]);
  readonly search = output<string>();

  readonly searchTerm = signal('');
  readonly isUserMenuOpen = signal(false);
  readonly isNotificationsOpen = signal(false);

  onSearchSubmit(): void {
    const term = this.searchTerm().trim();
    if (term) {
      this.search.emit(term);
    }
  }

  toggleUserMenu(): void {
    this.isUserMenuOpen.update((isOpen) => !isOpen);
    this.isNotificationsOpen.set(false);
  }

  toggleNotifications(): void {
    this.isNotificationsOpen.update((isOpen) => !isOpen);
    this.isUserMenuOpen.set(false);
  }

  closeMenus(): void {
    this.isUserMenuOpen.set(false);
    this.isNotificationsOpen.set(false);
  }

  onEscape(): void {
    this.closeMenus();
  }

  logout(): void {
    this.auth.logout();
  }
}
