import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';

import { AuthService } from '../../../../core/services/auth.service';
import { LoginRequest } from '../../../../shared/models/login-request.model';

@Component({
  selector: 'app-login-page',
  imports: [FormsModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPage {
  private readonly auth = inject(AuthService);

  credentials: LoginRequest = { email: '', password: '' };
  isSubmitting = false;
  loginError = '';

  onSubmit(): void {
    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.loginError = '';

    this.auth.login(this.credentials).subscribe({
      next: () => this.isSubmitting = false,
      error: () => {
        this.isSubmitting = false;
        this.loginError = 'Unable to log in. Please check your email and password.';
      },
    });
  }

}
