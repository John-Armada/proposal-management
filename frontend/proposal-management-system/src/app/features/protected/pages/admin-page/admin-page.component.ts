import { Component } from '@angular/core';
import { UserManagementComponent } from '../../../../admin/features/user-management/user-management.component';

@Component({
  imports: [UserManagementComponent],
  selector: 'app-admin-page',
  styleUrl: './admin-page.component.scss',
  templateUrl: './admin-page.component.html',
})
export class AdminPage {}
