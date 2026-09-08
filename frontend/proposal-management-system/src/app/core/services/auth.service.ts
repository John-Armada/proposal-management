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
export class Auth {
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
                                })
                        );
    }

    private restoreSession (): AuthSession | null {
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
        const claims = decodeJwt(response.token);
        const role = claims['roles'];

        if (!isAppUserRole(role)){
            throw new Error(`Invalid role in access token: ${role}`);
        }

        return {
            userId: claims['sub'],
            email: claims['email'] ?? '',
            firstName: claims['firstName'] ?? '',
            lastName: claims['lastName'] ?? '',
            role,
            deptId: claims['deptId'],
            accessToken: response.token,
            tokenType: response.tokenType,
            expiresAt: Date.now() + response.expiresInSeconds * 1000
        };
    }

    private persistSession(session: AuthSession): void {
        const store = localStorage
        store.setItem(STORAGE_KEY, JSON.stringify(session));
    }
}
