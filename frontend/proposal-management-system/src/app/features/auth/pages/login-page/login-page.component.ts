import { Component, inject } from '@angular/core';
import { HttpErrorResponse } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { AuthService } from '../../../../core/services/auth.service';
import { LoginRequest } from '../../../../shared/models/login-request.model';
import { APP_ICONS } from '../../../../core/icons/app-icons';

@Component({
  selector: 'app-login-page',
  imports: [FormsModule, FontAwesomeModule],
  templateUrl: './login-page.component.html',
  styleUrl: './login-page.component.scss',
})
export class LoginPage {
  private readonly auth = inject(AuthService);

  protected readonly icons = APP_ICONS;

  credentials: LoginRequest = { email: '', password: '' };
  isSubmitting = false;
  loginError = '';
  isPasswordVisible = false;

  togglePasswordVisibility(): void {
    this.isPasswordVisible = !this.isPasswordVisible;
  }

  onSubmit(): void {
    if (this.isSubmitting) {
      return;
    }

    this.isSubmitting = true;
    this.loginError = '';

    this.auth.login(this.credentials).subscribe({
      next: () => this.isSubmitting = false,
      error: (error: HttpErrorResponse) => {
        this.isSubmitting = false;
        this.loginError = error.error?.message
          ?? 'Unable to log in. Please check your email and password.';
      },
    });
  }

}
