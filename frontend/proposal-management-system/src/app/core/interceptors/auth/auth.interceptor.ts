import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const authService = inject(AuthService);
  const token = authService.getAccessToken();

  if (!token) {
    return next(req);
  }

  const authenticatedRequest = req.clone({
    setHeaders: {
      Authorization: `Bearer ${token}`
    }
  });

  return next(authenticatedRequest).pipe(
      catchError((error: HttpErrorResponse) => {
          // If a request fails with 401 Unauthorized while authenticated, clean up and redirect
          if (error.status === 401 && authService.isAuthenticated()) {
            authService.logout(); // Already handles clearing storage and navigating to /login
          }
          return throwError(() => error);
      })
   );
};