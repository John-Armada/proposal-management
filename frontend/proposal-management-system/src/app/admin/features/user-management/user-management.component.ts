import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';
import { FontAwesomeModule } from '@fortawesome/angular-fontawesome';

import { UserService } from '../../service/user.service';
import { DepartmentLookup, Role, User, UserCreatePayload, UserUpdatePayload } from '../../models/user.model';
import { SidebarComponent } from '../../../shared/components/sidebar/sidebar.component';
import { Navbar } from '../../../shared/components/navbar/navbar.component';
import { DataTable } from '../../../shared/components/data-table/data-table.component';
import { DataTableColumn } from '../../../shared/components/data-table/data-table.model';
import { NavItem } from '../../../shared/models/nav-item.model';
import { APP_ICONS } from '../../../core/icons/app-icons';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, SidebarComponent, Navbar, DataTable, FontAwesomeModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss'
})
export class UserManagementComponent implements OnInit {
  readonly navItems: NavItem[] = [
    {
      label: 'User Management',
      route: '/app/admin',
      icon: APP_ICONS.users
    }
  ];

  protected readonly icons = APP_ICONS;

  readonly columns: DataTableColumn<User>[] = [
    { key: 'name', header: 'Name' },
    { key: 'email', header: 'Email' },
    { key: 'role', header: 'Role', type: 'badge', badge: (user) => ({ label: user.role, tone: 'slate' }) },
    { key: 'deptName', header: 'Department' },
    {
      key: 'active',
      header: 'Status',
      type: 'badge',
      badge: (user) => user.active
        ? { label: 'Active', tone: 'success' }
        : { label: 'Deactivated', tone: 'danger' }
    },
  ];

  readonly trackByUser = (_index: number, user: User): number => user.userId;

  readonly users = signal<User[]>([]);
  readonly activeDepartments = signal<DepartmentLookup[]>([]);
  readonly roles = Object.values(Role);
  readonly isLoadingUsers = signal(false);

  // Pagination
  readonly currentPage = signal(0);
  readonly pageSize = signal(10);
  readonly totalElements = signal(0);
  readonly totalPages = signal(0);

  // Modal / Form state
  showModal = false;
  isEditMode = false;
  selectedUserId: number | null = null;
  readonly userForm = inject(FormBuilder).nonNullable.group({
    firstName: ['', Validators.required],
    lastName: ['', Validators.required],
    email: ['', [Validators.required, Validators.email]],
    password: [''],
    role: [Role.AUTHOR, Validators.required],
    departmentId: [0, Validators.min(1)]
  });
  readonly errorMessage = signal('');

  private readonly userService = inject(UserService);

  ngOnInit(): void {
    this.loadUsers();
    this.loadActiveDepartments();
  }

  loadUsers(): void {
    this.isLoadingUsers.set(true);
    this.userService.getUsers(this.currentPage(), this.pageSize()).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.totalElements.set(res.totalElements);
        this.totalPages.set(res.totalPages);
        this.isLoadingUsers.set(false);
      },
      error: () => {
        this.errorMessage.set('Failed to load users.');
        this.isLoadingUsers.set(false);
      }
    });
  }

  loadActiveDepartments(): void {
    this.userService.getActiveDepartments().subscribe({
      next: (depts) => this.activeDepartments.set(depts),
      error: () => this.errorMessage.set('Failed to load active departments.')
    });
  }

  openCreateModal(): void {
    this.isEditMode = false;
    this.selectedUserId = null;
    this.userForm.reset({ role: Role.AUTHOR, departmentId: 0 });
    this.userForm.get('password')?.setValidators([Validators.required]);
    this.userForm.get('password')?.updateValueAndValidity();
    this.showModal = true;
  }

  openEditModal(user: User): void {
    this.isEditMode = true;
    this.selectedUserId = user.userId;
    this.userForm.patchValue({
      firstName: user.firstName,
      lastName: user.lastName,
      email: user.email,
      role: user.role,
      departmentId: user.deptId
    });
    this.userForm.get('password')?.clearValidators();
    this.userForm.get('password')?.updateValueAndValidity();
    this.showModal = true;
  }

  closeModal(): void {
    this.showModal = false;
    this.userForm.reset();
  }

  saveUser(): void {
    if (this.userForm.invalid) {
      this.userForm.markAllAsTouched();
      return;
    }

    if (this.isEditMode && this.selectedUserId !== null) {
      const { password: _, ...payload } = this.userForm.getRawValue();
      this.userService.updateUser(this.selectedUserId, payload).subscribe({
        next: () => {
          this.closeModal();
          this.loadUsers();
        },
        error: (error: HttpErrorResponse) => this.errorMessage.set(error.error?.message || 'Error updating user.')
      });
    } else {
      const payload: UserCreatePayload = this.userForm.getRawValue();
      this.userService.createUser(payload).subscribe({
        next: () => {
          this.closeModal();
          this.loadUsers();
        },
        error: (error: HttpErrorResponse) => this.errorMessage.set(error.error?.message || 'Error creating user.')
      });
    }
  }

  toggleStatus(user: User): void {
    const nextStatus = !user.active;
    const confirmMsg = nextStatus
      ? `Reactivate account for ${user.name}?`
      : `Deactivate account for ${user.name}? The user will be blocked from logging in.`;

    if (confirm(confirmMsg)) {
      this.userService.toggleUserStatus(user.userId, nextStatus).subscribe({
        next: () => this.loadUsers(),
        error: () => this.errorMessage.set('Failed to update user status.')
      });
    }
  }

  onPageChange(page: number): void {
    this.currentPage.set(page);
    this.loadUsers();
  }

  onPageSizeChange(size: number): void {
    this.pageSize.set(size);
    this.currentPage.set(0);
    this.loadUsers();
  }
}