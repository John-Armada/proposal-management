import { IconDefinition } from '@fortawesome/fontawesome-common-types';

export interface NavItem {
    label: string;
    route: string;
    icon: IconDefinition;
    badge?: string | number;
    roles?: string[];
}