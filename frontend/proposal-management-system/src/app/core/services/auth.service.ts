import { computed, inject, Service, signal } from '@angular/core';
import { decodeJwt } from '../../shared/utils/jwt.util'
import { AppUserRole, AuthResponse, AuthSession } from '../../shared/models/auth-session.model';
import { isAppUserRole } from '../../shared/utils/auth.util';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { map, Observable, tap } from 'rxjs';
import { LoginRequest } from '../../shared/models/login-request.model';

const STORAGE_KEY = "prp-session"

@Service()
export class AuthService {
    private readonly http = inject(HttpClient);
    private readonly router = inject(Router);

    private readonly _session = signal<AuthSession | null>(this.restoreSession());
    readonly session = this._session.asReadonly();

    readonly isAuthenticated = computed(() => {
        const session = this._session();
        return !!session && session.expiresAt > Date.now();
    });

    login(credentials: LoginRequest): Observable<AuthSession> {
        const body: LoginRequest = {
            email: credentials.email,
            password: credentials.password
        };

        return this.http.post<AuthResponse>('/api/auth/login', body)
            .pipe(
                map(response => this.buildSession(response)),
                tap(session => {
                    this.persistSession(session);
                    this._session.set(session);
                    void this.router.navigateByUrl(this.routeForRole(session.role));
                })
            );
    }

    logout(): void {
        const clearSessionAndRedirect = () => {
            localStorage.removeItem(STORAGE_KEY);
            this._session.set(null);
            void this.router.navigateByUrl('/login');
        };

        if (!this.getAccessToken()) {
            clearSessionAndRedirect();
            return;
        }

        this.http.post<void>('/api/auth/logout', {}).subscribe({
            next: clearSessionAndRedirect,
            error: clearSessionAndRedirect
        });
    }

    private restoreSession(): AuthSession | null {
        const token = localStorage.getItem(STORAGE_KEY);

        if (!token) {
            return null;
        }

        try {
            const session = JSON.parse(token) as AuthSession;
            if (session.expiresAt <= Date.now()) {
                localStorage.removeItem(STORAGE_KEY);
                return null;
            }
            return session;
        } catch {
            localStorage.removeItem(STORAGE_KEY);
            return null;
        }
    }

    private buildSession(response: AuthResponse): AuthSession {
        const claims = decodeJwt<Record<string, unknown>>(response.accessToken);
        const roles = claims['roles'];
        const role = Array.isArray(roles) ? roles[0] : roles;

        if (!isAppUserRole(role)) {
            throw new Error(`Invalid role in access token: ${role}`);
        }

        return {
            userId: String(claims['sub'] ?? ''),
            email: String(claims['email'] ?? ''),
            firstName: String(claims['firstName'] ?? ''),
            lastName: String(claims['lastName'] ?? ''),
            role,
            deptId: String(claims['dept_id'] ?? ''),
            accessToken: response.accessToken,
            tokenType: response.tokenType,
            expiresAt: Date.now() + response.expiresInSeconds * 1000
        };
    }

    private routeForRole(role: AppUserRole): string {
        switch (role) {
            case 'ADMIN': return '/app/admin';
            case 'REVIEWER': return '/app/reviewer';
            case 'AUTHOR': return '/app/author';
        }
    }

    private persistSession(session: AuthSession): void {
        const store = localStorage
        store.setItem(STORAGE_KEY, JSON.stringify(session));
    }

    getAccessToken(): string | null {
        return this._session()?.accessToken ?? null;
    }
}