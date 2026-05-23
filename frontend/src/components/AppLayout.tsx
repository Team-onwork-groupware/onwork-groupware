import { useEffect, useState, type ReactNode } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'
import { api } from '../lib/api'

const NAV = [
  { to: '/dashboard', label: '홈' },
  { to: '/hr', label: '인사' },
  { to: '/attendance', label: '근태' },
  { to: '/leave', label: '휴가' },
]

/** 공통 레이아웃 — 좌측 사이드바 + 상단 헤더. 모든 모듈 화면이 재사용한다. */
export default function AppLayout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [unread, setUnread] = useState(0)

  useEffect(() => {
    api.get<{ unread: number }>('/notifications/unread-count')
      .then((r) => setUnread(r.data.unread))
      .catch(() => setUnread(0))
  }, [])

  async function onLogout() {
    await logout()
    navigate('/login')
  }

  return (
    <div className="layout">
      <aside className="sidebar">
        <div className="sidebar-brand">OnWork</div>
        <nav>
          {NAV.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => 'nav-item' + (isActive ? ' active' : '')}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>
      <div className="main">
        <header className="topbar">
          <div className="topbar-spacer" />
          <span className="bell" data-testid="notif-bell" title="알림">
            🔔{unread > 0 && <span className="bell-badge">{unread}</span>}
          </span>
          <div className="topbar-user" data-testid="topbar-user">
            <span className="user-name">{user?.name ?? '사용자'}</span>
            <span className="user-role">{user?.position ?? user?.role}</span>
            <button className="btn-ghost" onClick={onLogout} data-testid="logout-button">
              로그아웃
            </button>
          </div>
        </header>
        <section className="content">{children}</section>
      </div>
    </div>
  )
}
