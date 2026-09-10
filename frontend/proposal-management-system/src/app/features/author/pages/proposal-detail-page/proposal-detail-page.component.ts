import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { catchError, combineLatest, of, switchMap, tap } from 'rxjs';
import { normalizeApiError } from '../../../../core/utils/api-error.util';
import { ProposalReviewPanelComponent } from '../../components/proposal-review-panel/proposal-review-panel.component';
import { ProposalStatusBadgeComponent } from '../../components/proposal-status-badge/proposal-status-badge.component';
import { Proposal, ReviewRequest } from '../../models/proposal.model';
import { ProposalService } from '../../service/proposal.service';

@Component({
  selector: 'app-proposal-detail-page',
  standalone: true,
  imports: [CurrencyPipe, RouterLink, ProposalStatusBadgeComponent, ProposalReviewPanelComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-detail-page.component.html',
  styleUrl: './proposal-detail-page.component.scss',
})
export class ProposalDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly proposalService = inject(ProposalService);

  protected readonly loading = signal(true);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly reviewSubmitting = signal(false);

  // TODO: source from the authenticated session once auth state is wired up.
  protected readonly canReview = signal(true);
  protected readonly currentUserId = signal<number | null>(null);

  /** Bumped after a successful review so the proposal is refetched — status changes server-side. */
  private readonly reload = signal(0);

  protected readonly proposal = toSignal(
    combineLatest([this.route.paramMap, toObservable(this.reload)]).pipe(
      switchMap(([params]) => {
        this.loading.set(true);
        this.errorMessage.set(null);
        const id = Number(params.get('id'));
        return this.proposalService.getById(id).pipe(
          catchError((err) => {
            this.errorMessage.set(normalizeApiError(err).message);
            return of(null);
          }),
        );
      }),
      tap(() => this.loading.set(false)),
    ),
    { initialValue: null as Proposal | null },
  );

  protected submitReview(request: ReviewRequest): void {
    const proposal = this.proposal();
    if (!proposal) {
      return;
    }
    this.reviewSubmitting.set(true);
    this.proposalService.review(proposal.id, request).subscribe({
      next: () => {
        this.reviewSubmitting.set(false);
        this.reload.update((n) => n + 1);
      },
      error: (err) => {
        this.reviewSubmitting.set(false);
        this.errorMessage.set(normalizeApiError(err).message);
      },
    });
  }
}