import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { AuthService } from '../../services/auth.service';
import { inject } from '@angular/core';
import { catchError, throwError } from 'rxjs';

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  if (req.url.endsWith('/api/auth/login')) {
    return next(req);
  }

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
          return throwError(() => error);
      })
   );
};