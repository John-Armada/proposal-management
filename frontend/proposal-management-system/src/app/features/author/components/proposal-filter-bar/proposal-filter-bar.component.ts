import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { PROPOSAL_STATUSES, ProposalStatus } from '../../models/proposal.model';

export interface DepartmentOption {
  id: number;
  name: string;
}

export interface ProposalFilterValue {
  status: ProposalStatus | null;
  departmentId: number | null;
}

@Component({
  selector: 'app-proposal-filter-bar',
  standalone: true,
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-filter-bar.component.html',
  styleUrl: './proposal-filter-bar.component.scss',
})
export class ProposalFilterBarComponent {
  readonly departments = input<DepartmentOption[]>([]);

  readonly filterChange = output<ProposalFilterValue>();

  protected readonly statuses = PROPOSAL_STATUSES;

  protected readonly form = new FormGroup({
    status: new FormControl<ProposalStatus | null>(null),
    departmentId: new FormControl<number | null>(null),
  });

  protected emitChange(): void {
    const { status, departmentId } = this.form.getRawValue();
    this.filterChange.emit({ status, departmentId });
  }

  protected reset(): void {
    this.form.reset({ status: null, departmentId: null });
    this.emitChange();
  }
}