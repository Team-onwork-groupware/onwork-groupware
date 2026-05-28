import { useCallback, useEffect, useMemo, useState } from 'react'
import AppLayout from '../components/AppLayout'
import { api } from '../lib/api'

interface ApprovalItem {
  type: 'LEAVE' | 'OVERTIME' | 'HR'
  refId: number
  title: string
  requesterName: string
  summary: string
  ageDays: number
  urgent: boolean
}

interface InboxResponse {
  total: number
  urgent: number
  items: ApprovalItem[]
}

interface BatchResult {
  type: string
  id: number
  ok: boolean
  error: string | null
}

const TYPE_LABEL: Record<string, string> = { LEAVE: '휴가', OVERTIME: '시간외', HR: '인사' }

function ageBadge(ageDays: number, urgent: boolean) {
  if (urgent) return <span className="badge resigned">🔴 긴급 {ageDays}일</span>
  if (ageDays >= 1) return <span className="badge inactive">⚠️ {ageDays}일</span>
  return <span className="badge active">신규</span>
}

export default function ApprovalsPage() {
  const [data, setData] = useState<InboxResponse | null>(null)
  const [selected, setSelected] = useState<Set<string>>(new Set())
  const [busy, setBusy] = useState(false)
  const [lastResult, setLastResult] = useState<string | null>(null)

  const key = (it: ApprovalItem) => `${it.type}:${it.refId}`

  const load = useCallback(async () => {
    const r = await api.get<InboxResponse>('/approvals/inbox')
    setData(r.data)
    setSelected(new Set())
  }, [])

  useEffect(() => {
    load()
  }, [load])

  const allKeys = useMemo(() => new Set((data?.items ?? []).map(key)), [data])
  const allSelected = data && data.items.length > 0 && selected.size === data.items.length

  function toggleAll() {
    if (allSelected) setSelected(new Set())
    else setSelected(new Set(allKeys))
  }

  function toggleOne(it: ApprovalItem) {
    const k = key(it)
    const next = new Set(selected)
    if (next.has(k)) next.delete(k)
    else next.add(k)
    setSelected(next)
  }

  async function batch(action: 'APPROVE' | 'REJECT') {
    if (selected.size === 0) return
    let reason: string | null = null
    if (action === 'REJECT') {
      reason = window.prompt(`선택한 ${selected.size}건의 반려/보류 사유를 입력하세요`)
      if (!reason) return
    }
    setBusy(true)
    try {
      const items = (data?.items ?? [])
        .filter((it) => selected.has(key(it)))
        .map((it) => ({ type: it.type, id: it.refId }))
      const res = await api.post<{
        total: number
        succeeded: number
        failed: number
        results: BatchResult[]
      }>('/approvals/batch', { items, action, reason })
      const r = res.data
      setLastResult(`${action === 'APPROVE' ? '승인' : '반려/보류'} 결과 — 성공 ${r.succeeded} / 실패 ${r.failed}`)
      await load()
    } catch (err) {
      setLastResult('처리 실패: ' + (err as Error).message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <AppLayout>
      <h1 className="page-title">통합 결재함</h1>
      <p className="page-sub">
        휴가 · 시간외 · 인사 변경을 한 화면에서 처리합니다. <strong>긴급(2일 이상 대기)</strong>이 위로 정렬됩니다.
      </p>

      <section className="card-block" data-testid="approvals-inbox">
        <div className="approvals-header">
          <h2 className="section-title">
            대기 {data?.total ?? 0}건
            {data && data.urgent > 0 && <span className="urgent-pill">🔴 긴급 {data.urgent}건</span>}
          </h2>
          <div className="batch-actions">
            <span className="muted small">선택 {selected.size}건</span>
            <button
              className="btn-sm primary"
              disabled={busy || selected.size === 0}
              onClick={() => batch('APPROVE')}
            >
              선택 승인
            </button>
            <button className="btn-sm" disabled={busy || selected.size === 0} onClick={() => batch('REJECT')}>
              선택 반려/보류
            </button>
          </div>
        </div>

        {lastResult && <p className="muted small" style={{ marginBottom: 8 }}>{lastResult}</p>}

        {!data || data.items.length === 0 ? (
          <p className="muted">처리할 결재가 없습니다.</p>
        ) : (
          <table className="data-table">
            <thead>
              <tr>
                <th style={{ width: 32 }}>
                  <input type="checkbox" checked={!!allSelected} onChange={toggleAll} />
                </th>
                <th>유형</th>
                <th>요청자</th>
                <th>내용</th>
                <th>경과</th>
              </tr>
            </thead>
            <tbody>
              {data.items.map((it) => (
                <tr key={key(it)} className={it.urgent ? 'row-urgent' : ''}>
                  <td>
                    <input
                      type="checkbox"
                      checked={selected.has(key(it))}
                      onChange={() => toggleOne(it)}
                    />
                  </td>
                  <td>{TYPE_LABEL[it.type] ?? it.type}</td>
                  <td>{it.requesterName}</td>
                  <td>{it.title} · <span className="muted small">{it.summary}</span></td>
                  <td>{ageBadge(it.ageDays, it.urgent)}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </section>
    </AppLayout>
  )
}
