import { CurrencyPipe } from '@angular/common';
import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { Proposal } from '../../models/proposal.model';
import { ProposalStatusBadgeComponent } from '../proposal-status-badge/proposal-status-badge.component';

@Component({
  selector: 'app-proposal-table',
  standalone: true,
  imports: [CurrencyPipe, ProposalStatusBadgeComponent],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-table.component.html',
  styleUrl: './proposal-table.component.scss',
})
export class ProposalTableComponent {
  readonly proposals = input.required<Proposal[]>();
  readonly loading = input(false);

  readonly rowSelect = output<Proposal>();
}