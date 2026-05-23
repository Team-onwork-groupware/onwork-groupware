import { Navigate, Route, Routes } from 'react-router-dom'
import './App.css'

// Phase 0 셸 — 라우트 골격만. Phase 1~5에서 로그인/대시보드/인사/근태/휴가 화면이 채워진다.
function Placeholder({ title }: { title: string }) {
  return (
    <main style={{ fontFamily: 'system-ui, sans-serif', padding: '2rem' }}>
      <h1 style={{ fontSize: '1.4rem' }}>OnWork — {title}</h1>
      <p style={{ color: '#6B7280' }}>Phase 0 스켈레톤. 이후 단계에서 구현됩니다.</p>
    </main>
  )
}

export default function App() {
  return (
    <Routes>
      <Route path="/" element={<Navigate to="/login" replace />} />
      <Route path="/login" element={<Placeholder title="로그인" />} />
      <Route path="/dashboard" element={<Placeholder title="대시보드" />} />
      <Route path="*" element={<Placeholder title="404" />} />
    </Routes>
  )
}
