import { ChangeDetectionStrategy, Component, inject, signal } from '@angular/core';
import { toSignal } from '@angular/core/rxjs-interop';
import {
  FormControl,
  FormGroup,
  ReactiveFormsModule,
  Validators,
} from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { switchMap, of, tap, catchError } from 'rxjs';
import { normalizeApiError } from '../../../../core/utils/api-error.util';
import { CreateProposalRequest, Proposal, UpdateProposalRequest } from '../../models/proposal.model';
import { ProposalService } from '../../service/proposal.service';

interface ProposalFormControls {
  requestId: FormControl<number | null>;
  accountId: FormControl<number | null>;
  departmentId: FormControl<number | null>;
  offeringId: FormControl<number | null>;
  templateId: FormControl<number | null>;
  categoryId: FormControl<number | null>;
  title: FormControl<string>;
  description: FormControl<string>;
  googleDocUrl: FormControl<string>;
  contractValue: FormControl<number | null>;
  projectDuration: FormControl<number | null>;
  totalResources: FormControl<number | null>;
}

@Component({
  selector: 'app-proposal-form-page',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink],
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './proposal-form-page.component.html',
  styleUrl: './proposal-form-page.component.scss',
})
export class ProposalFormPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly router = inject(Router);
  private readonly proposalService = inject(ProposalService);

  protected readonly saving = signal(false);
  protected readonly errorMessage = signal<string | null>(null);
  protected readonly fieldErrors = signal<Record<string, string>>({});

  protected readonly proposalId = toSignal(
    this.route.paramMap.pipe(
      switchMap((params) => {
        const raw = params.get('id');
        if (!raw || raw === 'new') {
          return of(null);
        }
        return this.proposalService.getById(Number(raw)).pipe(
          tap((proposal) => this.patchForm(proposal)),
          catchError((err) => {
            this.errorMessage.set(normalizeApiError(err).message);
            return of(null);
          }),
        );
      }),
    ),
    { initialValue: null as Proposal | null },
  );

  protected readonly isEdit = () => this.proposalId() !== null;

  protected readonly form = new FormGroup<ProposalFormControls>({
    requestId: new FormControl<number | null>(null, { validators: [Validators.required] }),
    accountId: new FormControl<number | null>(null, { validators: [Validators.required] }),
    departmentId: new FormControl<number | null>(null, { validators: [Validators.required] }),
    offeringId: new FormControl<number | null>(null, { validators: [Validators.required] }),
    templateId: new FormControl<number | null>(null),
    categoryId: new FormControl<number | null>(null),
    title: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(255)],
    }),
    description: new FormControl('', { nonNullable: true }),
    googleDocUrl: new FormControl('', { nonNullable: true }),
    contractValue: new FormControl<number | null>(null, { validators: [Validators.min(0)] }),
    projectDuration: new FormControl<number | null>(null, {
      validators: [Validators.min(1), Validators.max(120)],
    }),
    totalResources: new FormControl<number | null>(null, {
      validators: [Validators.min(1), Validators.max(1000)],
    }),
  });

  private patchForm(proposal: Proposal): void {
    this.form.patchValue({
      requestId: proposal.requestId,
      accountId: proposal.accountId,
      departmentId: proposal.departmentId,
      offeringId: proposal.offeringId,
      templateId: proposal.templateId,
      categoryId: proposal.categoryId,
      title: proposal.title,
      description: proposal.description ?? '',
      googleDocUrl: proposal.googleDocUrl ?? '',
      contractValue: proposal.contractValue,
      projectDuration: proposal.projectDuration,
      totalResources: proposal.totalResources,
    });
    // requestId/accountId can't move once a proposal is being edited.
    this.form.controls.requestId.disable();
    this.form.controls.accountId.disable();
  }

  protected submit(): void {
    if (this.form.invalid) {
      this.form.markAllAsTouched();
      return;
    }

    this.saving.set(true);
    this.errorMessage.set(null);
    this.fieldErrors.set({});

    const raw = this.form.getRawValue();
    const existing = this.proposalId();

    if (existing) {
      const request: UpdateProposalRequest = {
        title: raw.title,
        description: raw.description || null,
        googleDocUrl: raw.googleDocUrl || null,
        contractValue: raw.contractValue,
        projectDuration: raw.projectDuration,
        totalResources: raw.totalResources,
        categoryId: raw.categoryId,
        departmentId: raw.departmentId,
        offeringId: raw.offeringId,
      };
      this.proposalService.update(existing.id, request).subscribe(this.saveObserver(existing.id));
    } else {
      const request: CreateProposalRequest = {
        requestId: raw.requestId!,
        accountId: raw.accountId!,
        departmentId: raw.departmentId!,
        offeringId: raw.offeringId!,
        templateId: raw.templateId,
        categoryId: raw.categoryId,
        title: raw.title,
        description: raw.description || null,
        googleDocUrl: raw.googleDocUrl || null,
        contractValue: raw.contractValue,
        projectDuration: raw.projectDuration,
        totalResources: raw.totalResources,
      };
      this.proposalService.create(request).subscribe({
        next: (created) => this.saveObserver(created.id).next(),
        error: (err) => this.handleSaveError(err),
      });
    }
  }

  private saveObserver(id: number) {
    return {
      next: () => {
        this.saving.set(false);
        this.router.navigate(['/proposals', id]);
      },
      error: (err: unknown) => this.handleSaveError(err),
    };
  }

  private handleSaveError(err: unknown): void {
    this.saving.set(false);
    const { message, field } = normalizeApiError(err);
    if (field && field in this.form.controls) {
      this.fieldErrors.set({ [field]: message });
    } else {
      this.errorMessage.set(message);
    }
  }
}