import AppLayout from '../components/AppLayout'
import { useAuth } from '../lib/auth'

export default function DashboardPage() {
  const { user } = useAuth()
  return (
    <AppLayout>
      <h1 className="page-title">안녕하세요, {user?.name ?? ''}님</h1>
      <p className="page-sub">오늘 팀원 출근 현황을 확인하세요.</p>
      <div className="placeholder-card" data-testid="dashboard-placeholder">
        대시보드 위젯(팀 근태현황 · 내 출퇴근 · 잔여 연차 · 팀 알림)은 Phase 5에서 구현됩니다.
      </div>
    </AppLayout>
  )
}
