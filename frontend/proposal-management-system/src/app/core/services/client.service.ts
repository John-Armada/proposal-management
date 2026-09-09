import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClientAccount } from '../../shared/models/intake.model';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<ClientAccount[]> {
    return this.http.get<ClientAccount[]>('/api/accounts');
  }

  create(client: ClientAccount): Observable<ClientAccount> {
    return this.http.post<ClientAccount>('/api/accounts', client);
  }

  update(id: number, client: ClientAccount): Observable<ClientAccount> {
    return this.http.put<ClientAccount>(`/api/accounts/${id}`, client);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/accounts/${id}`);
  }
}