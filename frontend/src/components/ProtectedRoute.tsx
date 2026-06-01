import { Navigate } from 'react-router-dom'
import type { ReactNode } from 'react'
import { useAuth } from '../lib/auth'
import { isRole, type Role } from '../lib/roles'

/** 토큰이 없으면 로그인으로 보낸다. */
export default function ProtectedRoute({ children, roles }: { children: ReactNode; roles?: Role[] }) {
  const { user } = useAuth()
  const token = localStorage.getItem('onwork.accessToken')
  if (!token) {
    return <Navigate to="/login" replace />
  }
  if (roles && (!isRole(user?.role) || !roles.includes(user.role))) {
    return <Navigate to="/dashboard" replace />
  }
  return <>{children}</>
}
