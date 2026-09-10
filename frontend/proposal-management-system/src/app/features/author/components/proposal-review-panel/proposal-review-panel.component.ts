import { ChangeDetectionStrategy, Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { ReviewDecision, ReviewRequest } from '../../models/proposal.model';

@Component({
  selector: 'app-proposal-review-panel',
  standalone: true,
  imports: [ReactiveFormsModule],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-review-panel.component.html',
  styleUrl: './proposal-review-panel.component.scss',
})
export class ProposalReviewPanelComponent {
  /** Reviewer role must hold; the backend also blocks an author reviewing their own proposal. */
  readonly canReview = input(false);
  readonly isOwnProposal = input(false);
  readonly submitting = input(false);

  readonly decide = output<ReviewRequest>();

  protected readonly form = new FormGroup({
    comment: new FormControl<string>(''),
  });

  protected submit(decision: ReviewDecision): void {
    if (this.submitting()) {
      return;
    }
    const comment = this.form.controls.comment.value?.trim() || null;
    this.decide.emit({ decision, comment });
  }
}