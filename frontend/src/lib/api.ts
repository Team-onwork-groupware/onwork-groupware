import axios from 'axios'

// OnWork REST API 클라이언트. Base: /api/v1 (ADR-SYS-002: Bearer JWT)
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

// Access Token 자동 첨부 (Phase 1에서 로그인 시 저장)
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('onwork.accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  return config
})
