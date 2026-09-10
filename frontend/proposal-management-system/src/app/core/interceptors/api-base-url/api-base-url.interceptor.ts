import { HttpInterceptorFn } from '@angular/common/http';

import { environment } from '../../../../environments/environment';

export const apiBaseUrlInterceptor: HttpInterceptorFn = (request, next) => {
  const isApiRequest = request.url === '/api' || request.url.startsWith('/api/');

  if (!isApiRequest || !environment.apiBaseUrl) {
    return next(request);
  }

  return next(
    request.clone({
      url: `${environment.apiBaseUrl}${request.url}`,
    }),
  );
};
