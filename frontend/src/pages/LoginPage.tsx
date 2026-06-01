import { useState, type CSSProperties, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'

const ONWORK_STEPS = [
  {
    key: 'capture',
    label: '업무 입력',
    title: '근태·휴가·인사 요청을 표준 양식으로 시작',
    meta: '직원은 출퇴근, 휴가, 변경 요청을 한 곳에서 남깁니다.',
  },
  {
    key: 'route',
    label: '권한 라우팅',
    title: '역할에 맞는 승인자에게 자동 전달',
    meta: '팀장·HR·경영진은 자기 권한에 맞는 항목만 확인합니다.',
  },
  {
    key: 'review',
    label: '결재 판단',
    title: '원문과 변경 전후를 보고 승인·보류·반려',
    meta: '처리 전 영향과 사유를 확인하고 기록으로 남깁니다.',
  },
  {
    key: 'sync',
    label: '결과 반영',
    title: '잔여일·근태 집계·알림이 즉시 갱신',
    meta: '결과는 대시보드, 관련 화면, 알림으로 이어집니다.',
  },
]

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)
  const [leaving, setLeaving] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, password)
      setLeaving(true)
      const reduceMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches
      window.setTimeout(() => navigate('/dashboard'), reduceMotion ? 0 : 420)
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        '로그인에 실패했습니다'
      setError(message)
      setLoading(false)
    }
  }

  return (
    <div className="auth-screen">
      <section className="auth-visual" aria-label="OnWork 소개">
        <div className="auth-visual-inner">
          <h1 className="brand">OnWork</h1>
          <p className="auth-visual-copy">흩어진 그룹웨어 업무를 하나의 승인 기록으로 정리합니다.</p>
          <div className="product-story" aria-label="OnWork 업무 흐름 설명">
            <div className="story-header">
              <strong>OnWork는 업무 요청이 처리되는 과정을 끝까지 추적합니다.</strong>
              <span>입력, 권한 판단, 결재, 결과 반영이 같은 흐름 안에서 움직입니다.</span>
            </div>

            <div className="story-map" aria-hidden="true">
              <div className="story-node source">
                <span>요청</span>
                <strong>근태·휴가·인사</strong>
              </div>
              <div className="story-hub">
                <span>OnWork</span>
                <strong>업무 상태 엔진</strong>
              </div>
              <div className="story-node output">
                <span>결과</span>
                <strong>결재·알림·집계</strong>
              </div>
              <div className="story-stream" />
            </div>

            <ol className="story-steps">
              {ONWORK_STEPS.map((step, index) => (
                <li
                  className={`story-step ${step.key}`}
                  key={step.key}
                  style={{ '--story-index': index } as CSSProperties}
                >
                  <span className="story-step-index">{index + 1}</span>
                  <div>
                    <span className="story-step-label">{step.label}</span>
                    <strong>{step.title}</strong>
                    <small>{step.meta}</small>
                  </div>
                </li>
              ))}
            </ol>

            <div className="story-proof">
              <div>
                <span>역할별 화면</span>
                <strong>직원 · 팀장 · HR · CEO</strong>
              </div>
              <div>
                <span>문서 기준 기능</span>
                <strong>인사 · 근태 · 휴가 · 결재 · 알림 · 온보딩</strong>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section className="auth-panel" aria-label="로그인">
        <form className={'auth-card' + (leaving ? ' auth-card-success' : '')} onSubmit={onSubmit} data-testid="login-form">
          <div>
            <h2 className="auth-title">로그인</h2>
            <p className="auth-sub">회사 계정으로 OnWork에 접속하세요.</p>
          </div>

          <label className="field">
            <span>이메일</span>
            <input
              data-testid="login-email"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              placeholder="name@onwork.kr"
              autoComplete="username"
              required
            />
          </label>

          <label className="field">
            <span>비밀번호</span>
            <input
              data-testid="login-password"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              placeholder="비밀번호"
              autoComplete="current-password"
              required
            />
          </label>

          {error && (
            <p className="auth-error" data-testid="login-error">
              {error}
            </p>
          )}

          <button className="btn-primary" type="submit" disabled={loading} data-testid="login-submit">
            {leaving ? '대시보드로 이동 중...' : loading ? '로그인 중...' : '로그인'}
          </button>
        </form>
      </section>
    </div>
  )
}
