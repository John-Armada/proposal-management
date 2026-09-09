import { Component, inject, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { HttpErrorResponse } from '@angular/common/http';

import { UserService } from '../../service/user.service';
import { DepartmentLookup, Role, User, UserCreatePayload, UserUpdatePayload } from '../../models/user.model';

@Component({
  selector: 'app-user-management',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './user-management.component.html',
  styleUrl: './user-management.component.scss'
})
export class UserManagementComponent implements OnInit {
  readonly users = signal<User[]>([]);
  readonly activeDepartments = signal<DepartmentLookup[]>([]);
  readonly roles = Object.values(Role);

  // Pagination
  readonly currentPage = signal(0);
  readonly pageSize = 10;
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
    this.userService.getUsers(this.currentPage(), this.pageSize).subscribe({
      next: (res) => {
        this.users.set(res.content);
        this.totalElements.set(res.totalElements);
        this.totalPages.set(res.totalPages);
      },
      error: () => this.errorMessage.set('Failed to load users.')
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
}