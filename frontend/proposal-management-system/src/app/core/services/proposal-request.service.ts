import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ProposalRequest } from '../../shared/models/intake.model';

@Injectable({ providedIn: 'root' })
export class ProposalRequestService {
  private readonly http = inject(HttpClient);

  getAll(): Observable<ProposalRequest[]> {
    return this.http.get<ProposalRequest[]>('/api/proposal-requests');
  }

  create(req: ProposalRequest): Observable<ProposalRequest> {
    return this.http.post<ProposalRequest>('/api/proposal-requests', req);
  }

  update(id: number, req: ProposalRequest): Observable<ProposalRequest> {
    return this.http.put<ProposalRequest>(`/api/proposal-requests/${id}`, req);
  }

  delete(id: number): Observable<void> {
    return this.http.delete<void>(`/api/proposal-requests/${id}`);
  }
}