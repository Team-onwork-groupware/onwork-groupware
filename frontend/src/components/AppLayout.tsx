import { useCallback, useEffect, useState, type ReactNode } from 'react'
import { NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'
import { api } from '../lib/api'

const NAV = [
  { to: '/dashboard', label: '홈' },
  { to: '/hr', label: '인사' },
  { to: '/attendance', label: '근태' },
  { to: '/leave', label: '휴가' },
  { to: '/approvals', label: '결재함' },
]

interface DigestItem {
  id: number
  type: string
  message: string
  read: boolean
}
interface Digest {
  unread: number
  pendingApprovals: number
  longPending: number
  recentApproved: number
  recentItems: DigestItem[]
}

export default function AppLayout({ children }: { children: ReactNode }) {
  const { user, logout } = useAuth()
  const navigate = useNavigate()
  const [digest, setDigest] = useState<Digest | null>(null)
  const [open, setOpen] = useState(false)

  const fetchDigest = useCallback(async () => {
    try {
      const r = await api.get<Digest>('/notifications/digest')
      setDigest(r.data)
    } catch {
      setDigest(null)
    }
  }, [])

  useEffect(() => {
    fetchDigest()
  }, [fetchDigest])

  async function markAllRead() {
    await api.patch('/notifications/read-all')
    await fetchDigest()
  }

  async function onLogout() {
    await logout()
    navigate('/login')
  }

  const unread = digest?.unread ?? 0

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
          <div className="bell-wrap">
            <button
              className="bell"
              data-testid="notif-bell"
              onClick={() => { setOpen(!open); if (!open) fetchDigest() }}
              title="알림"
            >
              🔔{unread > 0 && <span className="bell-badge">{unread}</span>}
            </button>
            {open && digest && (
              <div className="bell-dropdown" data-testid="notif-dropdown">
                <div className="bell-summary">
                  <div><strong>{digest.pendingApprovals}</strong><span>결재 대기</span></div>
                  <div><strong>{digest.longPending}</strong><span>긴급</span></div>
                  <div><strong>{digest.unread}</strong><span>안읽음</span></div>
                </div>
                <ul className="bell-list">
                  {digest.recentItems.length === 0 ? (
                    <li className="muted small">최근 알림이 없습니다.</li>
                  ) : (
                    digest.recentItems.map((n) => (
                      <li key={n.id} className={n.read ? 'read' : 'unread'}>
                        <span className="bell-type">{n.type.split('_')[0]}</span>
                        <span>{n.message}</span>
                      </li>
                    ))
                  )}
                </ul>
                <div className="bell-actions">
                  <button className="btn-sm" onClick={markAllRead}>전체 읽음</button>
                </div>
              </div>
            )}
          </div>
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
