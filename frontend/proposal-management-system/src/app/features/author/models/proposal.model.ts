/**
 * Domain models for the Proposal feature.
 * Mirrors `ProposalResponseDto`, `CreateProposalRequestDto`, `UpdateProposalRequestDto`,
 * and the Spring `Page<T>` envelope described in the API integration guide.
 */

export type ProposalStatus =
    | 'DRAFT'
    | 'IN_REVIEW'
    | 'SENT'
    | 'WON'
    | 'LOST'
    | 'REJECTED'
    | 'APPROVED';

export const PROPOSAL_STATUSES: ProposalStatus[] = [
    'DRAFT',
    'IN_REVIEW',
    'SENT',
    'WON',
    'LOST',
    'REJECTED',
    'APPROVED',
];

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number; // zero-based current page
    numberOfElements: number;
    first: boolean;
    last: boolean;
    empty: boolean;
}

export interface Proposal {
    id: number;
    title: string;
    description: string | null;
    status: ProposalStatus;
    currentVersion: number;
    googleDocUrl: string | null;
    contractValue: number | null;
    projectDuration: number | null;
    totalResources: number | null;
    requestId: number;
    requirementsSummary: string | null;
    accountId: number;
    accountName: string;
    templateId: number | null;
    templateName: string | null;
    categoryId: number | null;
    categoryName: string | null;
    departmentId: number;
    departmentName: string;
    offeringId: number;
    offeringName: string;
}

export interface CreateProposalRequest {
    requestId: number;
    accountId: number;
    templateId?: number | null;
    title: string;
    description?: string | null;
    googleDocUrl?: string | null;
    contractValue?: number | null;
    projectDuration?: number | null;
    totalResources?: number | null;
    categoryId?: number | null;
    departmentId: number;
    offeringId: number;
}

/**
 * Every field is optional; the backend mapper ignores nulls, so `null`
 * explicitly means "leave unchanged" rather than "clear this field".
 */
export interface UpdateProposalRequest {
    title?: string | null;
    description?: string | null;
    googleDocUrl?: string | null;
    contractValue?: number | null;
    projectDuration?: number | null;
    totalResources?: number | null;
    status?: ProposalStatus | null;
    categoryId?: number | null;
    departmentId?: number | null;
    offeringId?: number | null;
}

export interface ProposalListParams {
    status?: ProposalStatus;
    departmentId?: number;
    page: number;
    size: number;
    sort?: string;
}

export type ReviewDecision = 'APPROVE' | 'REJECT';

export interface ReviewRequest {
    decision: ReviewDecision;
    comment?: string | null;
}

export interface ReviewResponse {
    id: number;
    decision: ReviewDecision;
    comment: string | null;
    decidedAt: string;
    proposalId: number;
    reviewerId: number;
}

export interface ApiError {
    timestamp: string;
    status: number;
    error: string;
    message: string;
    path: string;
}