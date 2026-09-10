import { NgTemplateOutlet } from '@angular/common';
import { Component, computed, contentChild, input, output, TemplateRef } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { APP_ICONS } from '../../../core/icons/app-icons';
import { DataTableColumn, DataTableColumnAlign } from '../../models/data-table.model';

@Component({
  selector: 'app-data-table',
  imports: [NgTemplateOutlet, FontAwesomeModule],
  styleUrl: './data-table.component.scss',
  templateUrl: './data-table.component.html',
})
export class DataTable<T = Record<string, unknown>> {

  protected readonly icons = APP_ICONS;

  readonly columns = input.required<DataTableColumn<T>[]>();
  readonly rows = input.required<T[]>();
  readonly trackByFn = input<(index: number, row: T) => unknown>((index) => index);

  readonly loading = input(false);
  readonly emptyMessage = input('No records found.');
  readonly emptyActionLabel = input<string>();
  readonly emptyAction = output<void>();

  readonly page = input(0);
  readonly pageSize = input(10);
  readonly pageSizeOptions = input<number[]>([10, 25, 50, 100]);
  readonly totalElements = input(0);
  readonly pageChange = output<number>();
  readonly pageSizeChange = output<number>();

  readonly rowActionsTemplate = contentChild<TemplateRef<{ $implicit: T }>>('rowActions');

  readonly totalPages = computed(() =>
    this.pageSize() > 0 ? Math.max(1, Math.ceil(this.totalElements() / this.pageSize())) : 1
  );

  readonly rangeStart = computed(() =>
    this.totalElements() === 0 ? 0 : this.page() * this.pageSize() + 1
  );

  readonly rangeEnd = computed(() =>
    Math.min(this.totalElements(), this.page() * this.pageSize() + this.rows().length)
  );

  columnAlign(column: DataTableColumn<T>): DataTableColumnAlign {
    return column.align ?? (column.type === 'mono' ? 'right' : 'left');
  }

  cellValue(column: DataTableColumn<T>, row: T): string {
    if (column.value) {
      return column.value(row);
    }
    const raw = (row as Record<string, unknown>)[column.key];
    return raw === null || raw === undefined ? '' : String(raw);
  }

  onPageSizeChange(size: number): void {
    if (size !== this.pageSize()) {
      this.pageSizeChange.emit(size);
    }
  }

  onPageSizeSelect(event: Event): void {
    const target = event.target;
    if (target instanceof HTMLSelectElement) {
      this.onPageSizeChange(Number(target.value));
    }
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages() || page === this.page()) {
      return;
    }
    this.pageChange.emit(page);
  }
}
