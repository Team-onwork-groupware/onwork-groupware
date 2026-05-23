import { useEffect, useState } from 'react'
import AppLayout from '../components/AppLayout'
import { api } from '../lib/api'
import { useAuth } from '../lib/auth'

interface Summary {
  userName: string
  role: string
  clockedIn: boolean
  clockOutAt: string | null
  attendanceStatus: string
  annualTotal: number
  annualUsed: number
  annualRemaining: number
  unreadNotifications: number
  pendingApprovals: number
  teamAnomaliesToday: number
}

function Stat({ label, value, hint }: { label: string; value: string | number; hint?: string }) {
  return (
    <div className="stat-card">
      <span className="stat-label">{label}</span>
      <strong className="stat-value">{value}</strong>
      {hint && <span className="stat-hint">{hint}</span>}
    </div>
  )
}

export default function DashboardPage() {
  const { user } = useAuth()
  const [s, setS] = useState<Summary | null>(null)
  const managerUp = user ? ['CEO', 'VP', 'HR_MANAGER', 'MANAGER'].includes(user.role) : false

  useEffect(() => {
    api.get<Summary>('/dashboard/summary').then((r) => setS(r.data))
  }, [])

  const attendanceText = !s ? '-' : s.clockOutAt ? '퇴근 완료' : s.clockedIn ? '근무 중' : '미출근'

  return (
    <AppLayout>
      <h1 className="page-title">안녕하세요, {s?.userName ?? user?.name ?? ''}님</h1>
      <p className="page-sub">오늘의 근무 현황과 처리할 일을 확인하세요.</p>

      <div className="stat-grid" data-testid="dashboard-widgets">
        <Stat label="오늘 근태" value={attendanceText} hint={s?.attendanceStatus === 'ANOMALY' ? '이상 있음' : ''} />
        <Stat label="연차 잔여" value={s ? `${s.annualRemaining}일` : '-'} hint={s ? `총 ${s.annualTotal} · 사용 ${s.annualUsed}` : ''} />
        <Stat label="결재 대기" value={s ? s.pendingApprovals : '-'} hint="내가 처리할 결재" />
        <Stat label="안읽은 알림" value={s ? s.unreadNotifications : '-'} />
        {managerUp && <Stat label="팀 근태 이상" value={s ? s.teamAnomaliesToday : '-'} hint="오늘" />}
      </div>

      <div className="dash-links">
        <a href="/attendance" className="dash-link">근태 →</a>
        <a href="/leave" className="dash-link">휴가 →</a>
        <a href="/hr" className="dash-link">인사 →</a>
      </div>
    </AppLayout>
  )
}
