export type DataTableColumnType = 'text' | 'mono' | 'badge';
export type DataTableColumnAlign = 'left' | 'right';
export type DataTableBadgeTone = 'success' | 'warning' | 'danger' | 'info' | 'slate';

export interface DataTableBadge {
    label: string;
    tone: DataTableBadgeTone;
    outline?: boolean;
}

export interface DataTableColumn<T> {
    key: string;
    header: string;
    align?: DataTableColumnAlign;
    type?: DataTableColumnType;
    value?: (row: T) => string;
    badge?: (row: T) => DataTableBadge;
}
