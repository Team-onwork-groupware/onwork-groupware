export type Role = 'CEO' | 'VP' | 'HR_MANAGER' | 'MANAGER' | 'EMPLOYEE'

export const APPROVER_ROLES: Role[] = ['CEO', 'VP', 'HR_MANAGER', 'MANAGER']

export function isRole(value: string | null | undefined): value is Role {
  return value === 'CEO' || value === 'VP' || value === 'HR_MANAGER' || value === 'MANAGER' || value === 'EMPLOYEE'
}

export function isExecutive(role: string | null | undefined): boolean {
  return role === 'CEO' || role === 'VP'
}

export function isApprover(role: string | null | undefined): boolean {
  return isRole(role) && APPROVER_ROLES.includes(role)
}

export function hrSurfaceLabel(role: string | null | undefined): string {
  if (role === 'EMPLOYEE') return '마이페이지'
  if (role === 'MANAGER') return '팀원'
  return '인사'
}
