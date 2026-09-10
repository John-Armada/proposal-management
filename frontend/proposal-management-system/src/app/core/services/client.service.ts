import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Account } from '../../shared/models/intake.model';

@Injectable({ providedIn: 'root' })
export class AccountService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<Account[]> {
    return this.http.get<Account[]>('/api/accounts');
  }

  create(account: Account): Observable<Account> {
    return this.http.post<Account>('/api/accounts', account);
  }

  update(id: number, account: Account): Observable<Account> {
    return this.http.put<Account>(`/api/accounts/${id}`, account);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/accounts/${id}`);
  }
}