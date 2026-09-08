import { Component, inject } from '@angular/core';
import { RouterOutlet } from '@angular/router';

import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-protected-layout',
  imports: [RouterOutlet],
  templateUrl: './protected-layout.component.html',
})
export class ProtectedLayout {
  private readonly auth = inject(AuthService);

  logout(): void {
    this.auth.logout();
  }
}
