import { createContext, useContext, useState, type ReactNode } from 'react'
import { api } from './api'

export interface UserInfo {
  id: number
  name: string
  email: string
  role: string
  position: string | null
  departmentName: string | null
}

interface LoginResponse {
  accessToken: string
  refreshToken: string
  user: UserInfo
}

interface AuthState {
  user: UserInfo | null
  login: (email: string, password: string) => Promise<void>
  logout: () => Promise<void>
}

const AuthContext = createContext<AuthState | undefined>(undefined)

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<UserInfo | null>(() => {
    const raw = localStorage.getItem('onwork.user')
    return raw ? (JSON.parse(raw) as UserInfo) : null
  })

  async function login(email: string, password: string) {
    const { data } = await api.post<LoginResponse>('/auth/login', { email, password })
    localStorage.setItem('onwork.accessToken', data.accessToken)
    localStorage.setItem('onwork.refreshToken', data.refreshToken)
    localStorage.setItem('onwork.user', JSON.stringify(data.user))
    setUser(data.user)
  }

  async function logout() {
    try {
      await api.post('/auth/logout')
    } catch {
      // 네트워크 실패해도 로컬 토큰은 제거
    }
    localStorage.removeItem('onwork.accessToken')
    localStorage.removeItem('onwork.refreshToken')
    localStorage.removeItem('onwork.user')
    setUser(null)
  }

  return <AuthContext.Provider value={{ user, login, logout }}>{children}</AuthContext.Provider>
}

// eslint-disable-next-line react-refresh/only-export-components
export function useAuth(): AuthState {
  const ctx = useContext(AuthContext)
  if (!ctx) throw new Error('useAuth must be used within AuthProvider')
  return ctx
}
