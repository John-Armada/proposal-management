import { ChangeDetectionStrategy, Component, computed, input, output } from '@angular/core';
import { PageResponse } from '../../models/proposal.model';

@Component({
  selector: 'app-pagination',
  standalone: true,
  changeDetection: ChangeDetectionStrategy.OnPush,
  templateUrl: './pagination.component.html',
  styleUrl: './pagination.component.scss',
})
export class PaginationComponent<T = unknown> {
  readonly page = input.required<PageResponse<T> | null>();

  readonly pageChange = output<number>();

  protected readonly rangeLabel = computed(() => {
    const page = this.page();
    if (!page || page.empty) {
      return 'No results';
    }
    const start = page.number * page.size + 1;
    const end = start + page.numberOfElements - 1;
    return `${start}–${end} of ${page.totalElements}`;
  });

  protected previous(): void {
    const page = this.page();
    if (page && !page.first) {
      this.pageChange.emit(page.number - 1);
    }
  }

  protected next(): void {
    const page = this.page();
    if (page && !page.last) {
      this.pageChange.emit(page.number + 1);
    }
  }
}