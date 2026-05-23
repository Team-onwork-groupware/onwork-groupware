import { useEffect, useState, useCallback } from 'react'
import AppLayout from '../components/AppLayout'
import { api } from '../lib/api'
import { useAuth } from '../lib/auth'

interface Employee {
  id: number
  employeeNo: string
  name: string
  email: string
  role: string
  position: string | null
  status: string
  departmentName: string | null
}

interface ChangeRequest {
  id: number
  changeType: string
  status: string
  reason: string | null
  payload: Record<string, unknown>
  requestedBy: number
}

const STATUS_LABEL: Record<string, string> = {
  ACTIVE: '재직', INACTIVE: '비활성', RESIGNED: '퇴사',
}

export default function HrPage() {
  const { user } = useAuth()
  const isExecutive = user?.role === 'CEO' || user?.role === 'VP'
  const canSeeInbox = isExecutive || user?.role === 'HR_MANAGER'

  const [employees, setEmployees] = useState<Employee[]>([])
  const [requests, setRequests] = useState<ChangeRequest[]>([])
  const [loading, setLoading] = useState(true)

  const load = useCallback(async () => {
    setLoading(true)
    try {
      const emp = await api.get<{ items: Employee[] }>('/hr/employees')
      setEmployees(emp.data.items)
      if (canSeeInbox) {
        const reqs = await api.get<{ items: ChangeRequest[] }>('/hr/change-requests', {
          params: { status: 'PENDING' },
        })
        setRequests(reqs.data.items)
      }
    } finally {
      setLoading(false)
    }
  }, [canSeeInbox])

  useEffect(() => {
    load()
  }, [load])

  async function process(id: number, action: 'APPROVE' | 'REJECT') {
    let reason: string | null = null
    if (action === 'REJECT') {
      reason = window.prompt('반려 사유를 입력하세요')
      if (!reason) return
    }
    await api.patch(`/hr/change-requests/${id}/process`, { action, reason })
    await load()
  }

  return (
    <AppLayout>
      <h1 className="page-title">인사관리</h1>

      {canSeeInbox && (
        <section className="card-block" data-testid="hr-inbox">
          <h2 className="section-title">결재함 (대기 {requests.length}건)</h2>
          {requests.length === 0 ? (
            <p className="muted">대기 중인 인사 변경 요청이 없습니다.</p>
          ) : (
            <table className="data-table">
              <thead>
                <tr><th>유형</th><th>대상/내용</th><th>사유</th>{isExecutive && <th>처리</th>}</tr>
              </thead>
              <tbody>
                {requests.map((r) => (
                  <tr key={r.id}>
                    <td>{r.changeType}</td>
                    <td>{(r.payload?.name as string) ?? `user#${r.payload?.target_user_id ?? '-'}`}</td>
                    <td>{r.reason ?? '-'}</td>
                    {isExecutive && (
                      <td className="actions">
                        <button className="btn-sm primary" onClick={() => process(r.id, 'APPROVE')}>승인</button>
                        <button className="btn-sm" onClick={() => process(r.id, 'REJECT')}>반려</button>
                      </td>
                    )}
                  </tr>
                ))}
              </tbody>
            </table>
          )}
        </section>
      )}

      <section className="card-block">
        <h2 className="section-title">직원 목록 ({employees.length})</h2>
        {loading ? (
          <p className="muted">불러오는 중…</p>
        ) : (
          <table className="data-table" data-testid="employee-table">
            <thead>
              <tr><th>사번</th><th>이름</th><th>부서</th><th>직급</th><th>권한</th><th>상태</th></tr>
            </thead>
            <tbody>
              {employees.map((e) => (
                <tr key={e.id}>
                  <td>{e.employeeNo}</td>
                  <td>{e.name}</td>
                  <td>{e.departmentName ?? '미분류'}</td>
                  <td>{e.position ?? '-'}</td>
                  <td>{e.role}</td>
                  <td>
                    <span className={'badge ' + e.status.toLowerCase()}>
                      {STATUS_LABEL[e.status] ?? e.status}
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </AppLayout>
  )
}
