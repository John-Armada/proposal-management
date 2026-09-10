import { HttpErrorResponse } from '@angular/common/http';
import { ApiError } from '../../features/author/models/proposal.model';

/**
 * Normalizes the backend's `{ timestamp, status, error, message, path }` error
 * shape. `message` for 400s is typically prefixed with the offending field,
 * e.g. "title: Proposal title is required" — this splits that out so a
 * template can map it to the matching form control.
 */
export interface NormalizedApiError {
    status: number;
    message: string;
    field: string | null;
}

export function normalizeApiError(err: unknown): NormalizedApiError {
    if (err instanceof HttpErrorResponse) {
        const body = err.error as ApiError | undefined;
        const rawMessage = body?.message ?? err.message ?? 'Something went wrong.';
        const [maybeField, ...rest] = rawMessage.split(':');
        const field = rest.length > 0 ? maybeField.trim() : null;
        const message = rest.length > 0 ? rest.join(':').trim() : rawMessage;

        switch (err.status) {
            case 400:
                return { status: 400, message, field };
            case 401:
                return { status: 401, message: 'Your session has expired. Please sign in again.', field: null };
            case 403:
                return { status: 403, message: body?.message ?? 'You do not have permission to do that.', field: null };
            case 404:
                return { status: 404, message: body?.message ?? 'That record could not be found.', field: null };
            case 409:
                return { status: 409, message: body?.message ?? 'This record changed elsewhere. Refresh and try again.', field: null };
            default:
                return { status: err.status || 500, message: 'Something went wrong on our end. Please try again.', field: null };
        }
    }
    return { status: 0, message: 'Unable to reach the server.', field: null };
}