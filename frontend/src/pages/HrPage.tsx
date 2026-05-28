import { useCallback, useEffect, useState } from 'react'
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

interface Department {
  id: number
  name: string
}

const STATUS_LABEL: Record<string, string> = { ACTIVE: '재직', INACTIVE: '비활성', RESIGNED: '퇴사' }

const EMPTY_FORM = {
  name: '',
  email: '',
  hireDate: '',
  departmentId: '',
  position: '',
  workGroupId: '1',
}

export default function HrPage() {
  const { user } = useAuth()
  const isExec = user?.role === 'CEO' || user?.role === 'VP'
  const isHrManager = user?.role === 'HR_MANAGER'
  const canSeeInbox = isExec || isHrManager

  const [employees, setEmployees] = useState<Employee[]>([])
  const [requests, setRequests] = useState<ChangeRequest[]>([])   // PENDING
  const [drafts, setDrafts] = useState<ChangeRequest[]>([])       // 내 임시저장
  const [departments, setDepartments] = useState<Department[]>([])
  const [loading, setLoading] = useState(true)

  const [showForm, setShowForm] = useState(false)
  const [editingDraftId, setEditingDraftId] = useState<number | null>(null)
  const [form, setForm] = useState({ ...EMPTY_FORM })
  const [suggestedNo, setSuggestedNo] = useState('')
  const [errors, setErrors] = useState<Record<string, boolean>>({})
  const [submitting, setSubmitting] = useState(false)

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
      if (isHrManager) {
        const drft = await api.get<{ items: ChangeRequest[] }>('/hr/change-requests/my-drafts')
        setDrafts(drft.data.items)
      }
    } finally {
      setLoading(false)
    }
  }, [canSeeInbox, isHrManager])

  useEffect(() => {
    load()
  }, [load])

  async function ensureFormDeps() {
    if (departments.length === 0) {
      const d = await api.get<{ items: Department[] }>('/hr/departments')
      setDepartments(d.data.items)
    }
    const sno = await api.get<{ employeeNo: string }>('/hr/change-requests/next-employee-no')
    setSuggestedNo(sno.data.employeeNo)
  }

  async function openNewForm() {
    setShowForm(true)
    setEditingDraftId(null)
    setForm({ ...EMPTY_FORM })
    setErrors({})
    await ensureFormDeps()
  }

  async function openDraft(d: ChangeRequest) {
    setShowForm(true)
    setEditingDraftId(d.id)
    const p = d.payload as Record<string, unknown>
    setForm({
      name: (p.name as string) ?? '',
      email: (p.email as string) ?? '',
      hireDate: (p.hire_date as string) ?? '',
      departmentId: p.department_id != null ? String(p.department_id) : '',
      position: (p.position as string) ?? '',
      workGroupId: p.work_group_id != null ? String(p.work_group_id) : '1',
    })
    setErrors({})
    await ensureFormDeps()
  }

  function closeForm() {
    setShowForm(false)
    setEditingDraftId(null)
    setErrors({})
  }

  function buildPayload(): Record<string, unknown> {
    return {
      name: form.name.trim(),
      email: form.email.trim(),
      hire_date: form.hireDate || null,
      department_id: form.departmentId ? Number(form.departmentId) : null,
      position: form.position.trim() || null,
      role: 'EMPLOYEE',
      work_group_id: Number(form.workGroupId) || 1,
    }
  }

  function validateForSubmit(): boolean {
    const errs: Record<string, boolean> = {}
    if (!form.name.trim()) errs.name = true
    if (!form.email.trim()) errs.email = true
    if (!form.hireDate) errs.hireDate = true
    setErrors(errs)
    return Object.keys(errs).length === 0
  }

  async function onSaveDraft() {
    if (submitting) return
    setSubmitting(true)
    try {
      const body = { changeType: 'CREATE', payload: buildPayload(), reason: '신규 입사 등록 (임시저장)' }
      if (editingDraftId != null) {
        await api.patch(`/hr/change-requests/${editingDraftId}/draft`, body)
      } else {
        const r = await api.post<{ id: number }>('/hr/change-requests/draft', body)
        setEditingDraftId(r.data.id)
      }
      await load()
      alert('임시저장 되었습니다')
    } catch (err) {
      alert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? '저장 실패')
    } finally {
      setSubmitting(false)
    }
  }

  async function onSubmitForApproval() {
    if (submitting) return
    if (!validateForSubmit()) {
      alert('필수 정보를 입력하세요 (이름·이메일·입사일)')
      return
    }
    // UC-HR-01 정상 흐름 6/7단계: 확인 모달 — 취소 시 폼 입력 보존 (A3)
    const ok = window.confirm(`${form.name} 님의 입사 등록을 경영진에게 승인 요청하시겠습니까?`)
    if (!ok) return
    setSubmitting(true)
    try {
      if (editingDraftId == null) {
        await api.post('/hr/change-requests', {
          changeType: 'CREATE',
          payload: buildPayload(),
          reason: '신규 입사 등록',
        })
      } else {
        await api.post(`/hr/change-requests/${editingDraftId}/submit`)
      }
      closeForm()
      await load()
      alert('승인 요청이 등록되었습니다')
    } catch (err) {
      alert((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? '요청 실패')
    } finally {
      setSubmitting(false)
    }
  }

  async function onDeleteDraft(id: number) {
    if (!window.confirm('임시저장을 삭제하시겠습니까?')) return
    await api.delete(`/hr/change-requests/${id}`)
    if (editingDraftId === id) closeForm()
    await load()
  }

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

      {/* 경영진/HR 결재함 */}
      {canSeeInbox && (
        <section className="card-block" data-testid="hr-inbox">
          <h2 className="section-title">결재함 (대기 {requests.length}건)</h2>
          {requests.length === 0 ? (
            <p className="muted">대기 중인 인사 변경 요청이 없습니다.</p>
          ) : (
            <table className="data-table">
              <thead><tr><th>유형</th><th>대상/내용</th><th>사유</th>{isExec && <th>처리</th>}</tr></thead>
              <tbody>
                {requests.map((r) => (
                  <tr key={r.id}>
                    <td>{r.changeType}</td>
                    <td>{(r.payload?.name as string) ?? `user#${r.payload?.target_user_id ?? '-'}`}</td>
                    <td>{r.reason ?? '-'}</td>
                    {isExec && (
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

      {/* HR_MANAGER 전용: 신규 입사 등록 영역 */}
      {isHrManager && (
        <section className="card-block" data-testid="hire-section">
          <div className="approvals-header">
            <h2 className="section-title">신규 입사 등록</h2>
            {!showForm && (
              <button className="btn-primary" onClick={openNewForm} data-testid="open-hire-form">
                + 신규 입사자 등록
              </button>
            )}
          </div>

          {drafts.length > 0 && (
            <>
              <p className="muted small">내 임시저장 ({drafts.length}건)</p>
              <table className="data-table">
                <thead><tr><th>유형</th><th>대상</th><th>사유</th><th>처리</th></tr></thead>
                <tbody>
                  {drafts.map((d) => (
                    <tr key={d.id}>
                      <td>{d.changeType}</td>
                      <td>{(d.payload?.name as string) ?? '-'}</td>
                      <td>{d.reason ?? '-'}</td>
                      <td className="actions">
                        <button className="btn-sm primary" onClick={() => openDraft(d)}>이어서 작성</button>
                        <button className="btn-sm" onClick={() => onDeleteDraft(d.id)}>삭제</button>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </>
          )}

          {showForm && (
            <div className="hire-form" data-testid="hire-form">
              <p className="muted small">
                {editingDraftId ? `임시저장 #${editingDraftId} 이어서 작성` : '신규 입사자 등록 — [임시저장]은 결재자에게 알림 안 가요'}
              </p>
              <div className="hire-grid">
                <label>
                  <span>이름 *</span>
                  <input className={errors.name ? 'invalid' : ''} value={form.name}
                         onChange={(e) => setForm({ ...form, name: e.target.value })} placeholder="홍길동" />
                </label>
                <label>
                  <span>사번 (자동 채번)</span>
                  <input value={suggestedNo} readOnly />
                </label>
                <label>
                  <span>이메일 *</span>
                  <input type="email" className={errors.email ? 'invalid' : ''} value={form.email}
                         onChange={(e) => setForm({ ...form, email: e.target.value })} placeholder="name@onwork.kr" />
                </label>
                <label>
                  <span>입사일 *</span>
                  <input type="date" className={errors.hireDate ? 'invalid' : ''} value={form.hireDate}
                         onChange={(e) => setForm({ ...form, hireDate: e.target.value })} />
                </label>
                <label>
                  <span>부서</span>
                  <select value={form.departmentId}
                          onChange={(e) => setForm({ ...form, departmentId: e.target.value })}>
                    <option value="">미분류 (승인 후 분류 가능)</option>
                    {departments.map((d) => (
                      <option key={d.id} value={d.id}>{d.name}</option>
                    ))}
                  </select>
                </label>
                <label>
                  <span>직급</span>
                  <input value={form.position}
                         onChange={(e) => setForm({ ...form, position: e.target.value })} placeholder="사원" />
                </label>
              </div>
              <div className="hire-actions">
                <button className="btn-ghost" onClick={closeForm} disabled={submitting}>닫기</button>
                <button className="btn-ghost" onClick={onSaveDraft} disabled={submitting} data-testid="save-draft">
                  임시저장
                </button>
                <button className="btn-primary" onClick={onSubmitForApproval} disabled={submitting} data-testid="submit-approval">
                  승인 요청
                </button>
              </div>
            </div>
          )}
        </section>
      )}

      <section className="card-block">
        <h2 className="section-title">직원 목록 ({employees.length})</h2>
        {loading ? (
          <p className="muted">불러오는 중…</p>
        ) : (
          <table className="data-table" data-testid="employee-table">
            <thead><tr><th>사번</th><th>이름</th><th>부서</th><th>직급</th><th>권한</th><th>상태</th></tr></thead>
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
