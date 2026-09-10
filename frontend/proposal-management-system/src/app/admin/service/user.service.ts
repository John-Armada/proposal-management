import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';

import {
    DepartmentLookup,
    PageResponse,
    User,
    UserCreatePayload,
    UserUpdatePayload,
} from '../models/user.model';

@Injectable({
    providedIn: 'root'
})
export class UserService {
    private readonly usersUrl = '/api/admin/users';
    private readonly departmentsUrl = '/api/users/departments/active';

    constructor(private readonly http: HttpClient) { }

    getUsers(page: number = 0, size: number = 10): Observable<PageResponse<User>> {
        const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
        return this.http.get<PageResponse<User>>(this.usersUrl, { params });
    }

    getUserById(id: number): Observable<User> {
        return this.http.get<User>(`${this.usersUrl}/${id}`);
    }

    createUser(payload: UserCreatePayload): Observable<User> {
        return this.http.post<User>(this.usersUrl, payload);
    }

    updateUser(id: number, payload: UserUpdatePayload): Observable<User> {
        return this.http.put<User>(`${this.usersUrl}/${id}`, payload);
    }

    toggleUserStatus(id: number, active: boolean): Observable<User> {
        return this.http.patch<User>(`${this.usersUrl}/${id}/status`, null, {
            params: new HttpParams().set('active', active.toString())
        });
    }

    getActiveDepartments(): Observable<DepartmentLookup[]> {
        return this.http.get<DepartmentLookup[]>(this.departmentsUrl);
    }
}
