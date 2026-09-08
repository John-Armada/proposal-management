import { AppUserRole, APP_USER_ROLES } from "../models/auth-session.model";


export function isAppUserRole(value: unknown): value is AppUserRole {
  return typeof value === 'string'
    && APP_USER_ROLES.includes(value as AppUserRole);
}