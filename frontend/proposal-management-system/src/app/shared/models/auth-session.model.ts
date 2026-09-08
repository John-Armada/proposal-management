export const APP_USER_ROLES = [
  'ADMIN',
  'REVIEWER',
  'AUTHOR',
] as const;

export type AppUserRole = typeof APP_USER_ROLES[number];

export interface AuthSession {
   userId: string;
   email: string;
   firstName: string;
   lastName: string;
   role: AppUserRole;
   deptId: string ;
   accessToken: string;
   tokenType: string;
   expiresAt: number; // epoch ms
}

export interface AuthResponse {
  accessToken: string;
    tokenType: string;
    expiresInSeconds: number;
}

