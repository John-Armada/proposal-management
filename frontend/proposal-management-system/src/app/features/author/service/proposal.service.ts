import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
    CreateProposalRequest,
    PageResponse,
    Proposal,
    ProposalListParams,
    ReviewRequest,
    ReviewResponse,
    UpdateProposalRequest,
} from '../models/proposal.model';

/**
 * Talks to `/api/proposals`. URLs stay relative so the app's base-url and
 * auth interceptors can prepend the configured host and bearer token.
 */
@Injectable({ providedIn: 'root' })
export class ProposalService {
    private readonly http = inject(HttpClient);
    private readonly baseUrl = '/api/proposals';

    getProposals(params: ProposalListParams): Observable<PageResponse<Proposal>> {
        let httpParams = new HttpParams()
            .set('page', params.page)
            .set('size', params.size);

        if (params.status) {
            httpParams = httpParams.set('status', params.status);
        }
        if (params.departmentId != null) {
            httpParams = httpParams.set('departmentId', params.departmentId);
        }
        if (params.sort) {
            httpParams = httpParams.set('sort', params.sort);
        }

        return this.http.get<PageResponse<Proposal>>(this.baseUrl, { params: httpParams });
    }

    getById(id: number): Observable<Proposal> {
        return this.http.get<Proposal>(`${this.baseUrl}/${id}`);
    }

    create(request: CreateProposalRequest): Observable<Proposal> {
        return this.http.post<Proposal>(this.baseUrl, request);
    }

    update(id: number, request: UpdateProposalRequest): Observable<Proposal> {
        return this.http.put<Proposal>(`${this.baseUrl}/${id}`, request);
    }

    review(id: number, request: ReviewRequest): Observable<ReviewResponse> {
        return this.http.post<ReviewResponse>(`${this.baseUrl}/${id}/review`, request);
    }
}