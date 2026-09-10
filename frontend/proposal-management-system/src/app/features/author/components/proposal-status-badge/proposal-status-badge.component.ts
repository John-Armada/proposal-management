import { ChangeDetectionStrategy, Component, computed, input } from '@angular/core';
import { ProposalStatus } from '../../models/proposal.model';

const STATUS_LABEL: Record<ProposalStatus, string> = {
  DRAFT: 'Draft',
  IN_REVIEW: 'In review',
  SENT: 'Sent',
  WON: 'Won',
  LOST: 'Lost',
  REJECTED: 'Rejected',
  APPROVED: 'Approved',
};

@Component({
  selector: 'app-proposal-status-badge',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-status-badge.component.html',
  styleUrl: './proposal-status-badge.component.scss',
})
export class ProposalStatusBadgeComponent {
  readonly status = input.required<ProposalStatus>();

  protected readonly label = computed(() => STATUS_LABEL[this.status()]);
  protected readonly modifierClass = computed(() => `status-badge--${this.status().toLowerCase().replace('_', '-')}`);
}