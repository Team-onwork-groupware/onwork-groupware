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

const TYPE_LABEL: Record<string, string> = { LEAVE: '휴가', OVERTIME: '시간외', HR: '인사' }
const TYPE_FILTERS: Array<'ALL' | ApprovalItem['type']> = ['ALL', 'LEAVE', 'OVERTIME', 'HR']

function ageBadge(ageDays: number, urgent: boolean) {
  if (urgent) return <span className="badge resigned">긴급 {ageDays}일</span>
  if (ageDays >= 1) return <span className="badge inactive">대기 {ageDays}일</span>
  return <span className="badge active">신규</span>
}

function approvalImpact(item: ApprovalItem) {
  if (item.type === 'LEAVE') return '승인 시 신청자의 휴가 잔여가 차감되고, 보류 시 사유가 신청자에게 알림으로 전달됩니다.'
  if (item.type === 'OVERTIME') return '승인 시 시간외 신청이 인정되고, 반려 시 반려 사유가 신청자 알림에 표시됩니다.'
  return '승인 시 인사 데이터가 실제 직원 정보에 반영되고, 반려 시 요청자에게 사유가 전달됩니다.'
}

function typeTone(type: ApprovalItem['type']) {
  if (type === 'LEAVE') return 'green'
  if (type === 'OVERTIME') return 'amber'
  return 'blue'
}

export default function ApprovalsPage() {
  const [data, setData] = useState<InboxResponse | null>(null)
  const [selected, setSelected] = useState<Set<string>>(new Set())
  const [detail, setDetail] = useState<ApprovalItem | null>(null)
  const [busy, setBusy] = useState(false)
  const [lastResult, setLastResult] = useState<string | null>(null)
  const [typeFilter, setTypeFilter] = useState<'ALL' | ApprovalItem['type']>('ALL')
  const [urgentOnly, setUrgentOnly] = useState(false)

  const key = (it: ApprovalItem) => `${it.type}:${it.refId}`

  const load = useCallback(async () => {
    const r = await api.get<InboxResponse>('/approvals')
    setData(r.data)
    setSelected(new Set())
  }, [])

  useEffect(() => {
    const timer = window.setTimeout(() => {
      void load()
    }, 0)
    return () => window.clearTimeout(timer)
  }, [load])

  const visibleItems = useMemo(() => {
    return (data?.items ?? []).filter((it) => (
      (typeFilter === 'ALL' || it.type === typeFilter) && (!urgentOnly || it.urgent)
    ))
  }, [data?.items, typeFilter, urgentOnly])

  const visibleKeys = useMemo(() => new Set(visibleItems.map(key)), [visibleItems])
  const visibleSelectedCount = visibleItems.filter((it) => selected.has(key(it))).length
  const allSelected = visibleItems.length > 0 && visibleSelectedCount === visibleItems.length

  function toggleAll() {
    const next = new Set(selected)
    if (allSelected) {
      visibleKeys.forEach((k) => next.delete(k))
    } else {
      visibleKeys.forEach((k) => next.add(k))
    }
    setSelected(next)
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
      const items = visibleItems
        .filter((it) => selected.has(key(it)))
      let succeeded = 0
      let failed = 0
      for (const it of items) {
        try {
          await api.patch(`/approvals/${it.refId}/process`, { type: it.type, action, reason })
          succeeded += 1
        } catch (err) {
          console.warn('approval process failed', err)
          failed += 1
        }
      }
      setLastResult(`${action === 'APPROVE' ? '승인' : '반려/보류'} 결과: 성공 ${succeeded} / 실패 ${failed}`)
      await load()
    } catch (err) {
      setLastResult('처리 실패: ' + (err as Error).message)
    } finally {
      setBusy(false)
    }
  }

  async function processOne(item: ApprovalItem, action: 'APPROVE' | 'REJECT') {
    let reason: string | null = null
    if (action === 'REJECT') {
      reason = window.prompt(item.type === 'LEAVE' ? '보류 사유를 입력하세요' : '반려 사유를 입력하세요')
      if (!reason) return
    }
    setBusy(true)
    try {
      await api.patch(`/approvals/${item.refId}/process`, { type: item.type, action, reason })
      setLastResult(`${item.title} ${action === 'APPROVE' ? '승인' : '반려/보류'} 완료`)
      setDetail(null)
      await load()
    } catch (err) {
      setLastResult('처리 실패: ' + ((err as { response?: { data?: { message?: string } } })?.response?.data?.message ?? (err as Error).message))
    } finally {
      setBusy(false)
    }
  }

  return (
    <AppLayout>
      <div className="work-screen">
        <section className="screen-hero approvals-hero">
          <div>
            <p className="screen-kicker">결재 라우팅 허브</p>
            <h1>통합 결재함</h1>
            <p>휴가 · 시간외 · 인사 변경을 한 화면에서 처리합니다. 긴급 항목은 즉시 확인할 수 있게 강조됩니다.</p>
          </div>
          <div className="approval-orbit">
            <span className="queue-pulse" />
            <strong>{data?.total ?? 0}</strong>
            <small>대기 중인 결재</small>
          </div>
        </section>

        <section className="metric-grid">
          <article className="metric-card tone-blue">
            <span className="metric-label">전체 대기</span>
            <strong className="metric-value">{data?.total ?? 0}</strong>
            <span className="metric-hint">현재 결재함</span>
          </article>
          <article className="metric-card tone-rose">
            <span className="metric-label">긴급</span>
            <strong className="metric-value">{data?.urgent ?? 0}</strong>
            <span className="metric-hint">2일 이상 대기</span>
          </article>
          <article className="metric-card tone-green">
            <span className="metric-label">선택</span>
            <strong className="metric-value">{visibleSelectedCount}</strong>
            <span className="metric-hint">일괄 처리 대상</span>
          </article>
          <article className="metric-card tone-amber">
            <span className="metric-label">필터 결과</span>
            <strong className="metric-value">{visibleItems.length}</strong>
            <span className="metric-hint">{typeFilter === 'ALL' ? '전체 유형' : TYPE_LABEL[typeFilter]}</span>
          </article>
        </section>

        <section className="card-block service-panel" data-testid="approvals-inbox">
          <div className="approvals-header">
            <div>
              <h2 className="section-title">
                결재 대기 목록
                {data && data.urgent > 0 && <span className="urgent-pill">긴급 {data.urgent}건</span>}
              </h2>
              <p className="muted small">행을 클릭하면 원문, 요약, 처리 후 영향을 확인합니다.</p>
            </div>
            <div className="batch-actions">
              <span className="count-chip">선택 {visibleSelectedCount}건</span>
              <button
                className="btn-sm primary"
                disabled={busy || visibleSelectedCount === 0}
                onClick={() => batch('APPROVE')}
              >
                선택 승인
              </button>
              <button className="btn-sm" disabled={busy || visibleSelectedCount === 0} onClick={() => batch('REJECT')}>
                선택 반려/보류
              </button>
            </div>
          </div>

          <div className="approval-filters" aria-label="결재 필터">
            <div className="segmented">
              {TYPE_FILTERS.map((filter) => (
                <button
                  key={filter}
                  className={typeFilter === filter ? 'active' : ''}
                  onClick={() => setTypeFilter(filter)}
                  type="button"
                >
                  {filter === 'ALL' ? '전체' : TYPE_LABEL[filter]}
                </button>
              ))}
            </div>
            <label className="check-filter">
              <input type="checkbox" checked={urgentOnly} onChange={(event) => setUrgentOnly(event.target.checked)} />
              긴급만
            </label>
          </div>

          {lastResult && <div className="toast-line">{lastResult}</div>}

          {!data || visibleItems.length === 0 ? (
            <div className="empty-state">처리할 결재가 없습니다.</div>
          ) : (
            <div className="table-shell">
              <table className="data-table">
                <thead>
                  <tr>
                    <th style={{ width: 32 }}>
                      <input type="checkbox" checked={!!allSelected} onChange={toggleAll} aria-label="전체 선택" />
                    </th>
                    <th>유형</th>
                    <th>요청자</th>
                    <th>내용</th>
                    <th>경과</th>
                  </tr>
                </thead>
                <tbody>
                  {visibleItems.map((it) => (
                    <tr
                      key={key(it)}
                      className={it.urgent ? 'row-urgent clickable-row' : 'clickable-row'}
                      onClick={() => setDetail(it)}
                    >
                      <td>
                        <input
                          type="checkbox"
                          checked={selected.has(key(it))}
                          onClick={(event) => event.stopPropagation()}
                          onChange={() => toggleOne(it)}
                          aria-label={`${it.title} 선택`}
                        />
                      </td>
                      <td><span className={`type-chip ${typeTone(it.type)}`}>{TYPE_LABEL[it.type] ?? it.type}</span></td>
                      <td><strong>{it.requesterName}</strong></td>
                      <td>{it.title} · <span className="muted small">{it.summary}</span></td>
                      <td>{ageBadge(it.ageDays, it.urgent)}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </section>
      </div>

      {detail && (
        <div className="modal-backdrop" role="presentation" onClick={() => setDetail(null)}>
          <section className="detail-drawer" role="dialog" aria-modal="true" aria-label="결재 상세" onClick={(event) => event.stopPropagation()}>
            <div className="detail-header">
              <div>
                <p className="muted small">결재 상세</p>
                <h2 className="section-title">{detail.title}</h2>
              </div>
              <button className="btn-ghost" onClick={() => setDetail(null)}>닫기</button>
            </div>
            <dl className="detail-list">
              <div><dt>유형</dt><dd>{TYPE_LABEL[detail.type] ?? detail.type}</dd></div>
              <div><dt>요청자</dt><dd>{detail.requesterName}</dd></div>
              <div><dt>업무 ID</dt><dd>{detail.refId}</dd></div>
              <div><dt>대기일</dt><dd>{detail.ageDays}일{detail.urgent ? ' · 긴급' : ''}</dd></div>
              <div className="detail-wide"><dt>요청 원문</dt><dd>{detail.title}</dd></div>
              <div className="detail-wide"><dt>요청 사유/기간</dt><dd>{detail.summary || '-'}</dd></div>
              <div className="detail-wide"><dt>처리 후 영향</dt><dd>{approvalImpact(detail)}</dd></div>
            </dl>
            <div className="detail-actions">
              <button className="btn-sm primary" disabled={busy} onClick={() => processOne(detail, 'APPROVE')}>승인</button>
              <button className="btn-sm" disabled={busy} onClick={() => processOne(detail, 'REJECT')}>
                {detail.type === 'LEAVE' ? '보류' : '반려'}
              </button>
            </div>
          </section>
        </div>
      )}
    </AppLayout>
  )
}
