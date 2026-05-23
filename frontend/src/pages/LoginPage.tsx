import { useState, type FormEvent } from 'react'
import { useNavigate } from 'react-router-dom'
import { useAuth } from '../lib/auth'

export default function LoginPage() {
  const { login } = useAuth()
  const navigate = useNavigate()
  const [email, setEmail] = useState('')
  const [password, setPassword] = useState('')
  const [error, setError] = useState('')
  const [loading, setLoading] = useState(false)

  async function onSubmit(e: FormEvent) {
    e.preventDefault()
    setError('')
    setLoading(true)
    try {
      await login(email, password)
      navigate('/dashboard')
    } catch (err) {
      const message =
        (err as { response?: { data?: { message?: string } } })?.response?.data?.message ??
        '로그인에 실패했습니다'
      setError(message)
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="auth-screen">
      <form className="auth-card" onSubmit={onSubmit} data-testid="login-form">
        <h1 className="brand">OnWork</h1>
        <p className="auth-sub">근태 · 휴가 · 인사 관리를 한 곳에서</p>

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
          {loading ? '로그인 중…' : '로그인'}
        </button>
      </form>
    </div>
  )
}
