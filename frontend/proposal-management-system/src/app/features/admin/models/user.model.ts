export enum Role {
    ADMIN = 'ADMIN',
    AUTHOR = 'AUTHOR',
    REVIEWER = 'REVIEWER'
}

export interface User {
    userId: number;
    firstName: string;
    lastName: string;
    name: string;
    email: string;
    role: Role;
    active: boolean;
    deptId: number;
    deptName: string;
}
export interface UserCreatePayload {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    role: Role;
    departmentId: number;
}

export interface UserUpdatePayload {
    firstName: string;
    lastName: string;
    email: string;
    role: Role;
    departmentId: number;
}

export interface DepartmentLookup {
    id: number;
    code: string;
    name: string;
}

export interface PageResponse<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}