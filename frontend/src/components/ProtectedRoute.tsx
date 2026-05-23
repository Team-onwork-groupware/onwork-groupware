import { Navigate } from 'react-router-dom'
import type { ReactNode } from 'react'

/** 토큰이 없으면 로그인으로 보낸다. */
export default function ProtectedRoute({ children }: { children: ReactNode }) {
  const token = localStorage.getItem('onwork.accessToken')
  if (!token) {
    return <Navigate to="/login" replace />
  }
  return <>{children}</>
}
