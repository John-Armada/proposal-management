import { NgTemplateOutlet } from '@angular/common';
import { Component, ContentChild, EventEmitter, Input, Output, TemplateRef } from '@angular/core';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { APP_ICONS } from '../../../core/icons/app-icons';
import { DataTableColumn, DataTableColumnAlign } from './data-table.model';

@Component({
  selector: 'app-data-table',
  standalone: true,
  imports: [NgTemplateOutlet, FontAwesomeModule],
  styleUrl: './data-table.component.scss',
  templateUrl: './data-table.component.html',
})
export class DataTable<T = Record<string, unknown>> {
  protected readonly icons = APP_ICONS;

  @Input({ required: true }) columns: DataTableColumn<T>[] = [];
  @Input({ required: true }) rows: T[] = [];
  @Input() trackByFn: (index: number, row: T) => unknown = (index) => index;

  @Input() loading = false;
  @Input() emptyMessage = 'No records found.';
  @Input() emptyActionLabel?: string;
  @Output() emptyAction = new EventEmitter<void>();

  @Input() page = 0;
  @Input() pageSize = 10;
  @Input() pageSizeOptions: number[] = [10, 25, 50, 100];
  @Input() totalElements = 0;
  @Output() pageChange = new EventEmitter<number>();
  @Output() pageSizeChange = new EventEmitter<number>();

  @ContentChild('rowActions', { read: TemplateRef })
  rowActionsTemplate?: TemplateRef<{ $implicit: T }>;

  get totalPages(): number {
    return this.pageSize > 0 ? Math.max(1, Math.ceil(this.totalElements / this.pageSize)) : 1;
  }

  get rangeStart(): number {
    return this.totalElements === 0 ? 0 : this.page * this.pageSize + 1;
  }

  get rangeEnd(): number {
    return Math.min(this.totalElements, this.page * this.pageSize + this.rows.length);
  }

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
    if (size !== this.pageSize) {
      this.pageSizeChange.emit(size);
    }
  }

  goToPage(page: number): void {
    if (page < 0 || page >= this.totalPages || page === this.page) {
      return;
    }
    this.pageChange.emit(page);
  }
}
