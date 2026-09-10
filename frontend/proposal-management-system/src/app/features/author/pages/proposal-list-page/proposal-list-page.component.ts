import { ChangeDetectionStrategy, Component, computed, inject, signal } from '@angular/core';
import { toObservable, toSignal } from '@angular/core/rxjs-interop';
import { Router } from '@angular/router';
import { catchError, of, switchMap, tap } from 'rxjs';
import { normalizeApiError } from '../../../../core/utils/api-error.util';
import {
  DepartmentOption,
  ProposalFilterBarComponent,
  ProposalFilterValue,
} from '../../components/proposal-filter-bar/proposal-filter-bar.component';
import { PaginationComponent } from '../../components/pagination/pagination.component';
import { ProposalTableComponent } from '../../components/proposal-table/proposal-table.component';
import { PageResponse, Proposal } from '../../models/proposal.model';
import { ProposalService } from '../../service/proposal.service';

const PAGE_SIZE = 20;
const DEFAULT_SORT = 'id,desc';

@Component({
  selector: 'app-proposal-list-page',
  standalone: true,
  imports: [ProposalFilterBarComponent, ProposalTableComponent, PaginationComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-list-page.component.html',
  styleUrl: './proposal-list-page.component.scss',
})
export class ProposalListPageComponent {
  private readonly proposalService = inject(ProposalService);
  private readonly router = inject(Router);

  // TODO: replace with a real DepartmentService lookup once that endpoint is confirmed.
  protected readonly departments: DepartmentOption[] = [];

  private readonly page = signal(0);
  private readonly filter = signal<ProposalFilterValue>({ status: null, departmentId: null });
  protected readonly loading = signal(false);
  protected readonly errorMessage = signal<string | null>(null);

  private readonly request = computed(() => ({
    page: this.page(),
    size: PAGE_SIZE,
    sort: DEFAULT_SORT,
    status: this.filter().status ?? undefined,
    departmentId: this.filter().departmentId ?? undefined,
  }));

  protected readonly proposalPage = toSignal(
    toObservable(this.request).pipe(
      tap(() => {
        this.loading.set(true);
        this.errorMessage.set(null);
      }),
      switchMap((params) =>
        this.proposalService.getProposals(params).pipe(
          catchError((err) => {
            this.errorMessage.set(normalizeApiError(err).message);
            return of(null);
          }),
        ),
      ),
      tap(() => this.loading.set(false)),
    ),
    { initialValue: null as PageResponse<Proposal> | null },
  );

  protected onFilterChange(value: ProposalFilterValue): void {
    this.filter.set(value);
    this.page.set(0);
  }

  protected onPageChange(page: number): void {
    this.page.set(page);
  }

  protected openProposal(proposal: Proposal): void {
    this.router.navigate(['/proposals', proposal.id]);
  }

  protected createProposal(): void {
    this.router.navigate(['/proposals', 'new']);
  }
}