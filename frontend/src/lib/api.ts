import axios from 'axios'

type JsonObject = Record<string, unknown>

function isPlainObject(value: unknown): value is JsonObject {
  return Object.prototype.toString.call(value) === '[object Object]'
}

function toCamel(key: string): string {
  return key.replace(/_([a-z])/g, (_, char: string) => char.toUpperCase())
}

function toSnake(key: string): string {
  return key.replace(/[A-Z]/g, (char) => `_${char.toLowerCase()}`)
}

function convertKeys(value: unknown, convert: (key: string) => string): unknown {
  if (Array.isArray(value)) return value.map((item) => convertKeys(item, convert))
  if (!isPlainObject(value)) return value
  return Object.fromEntries(
    Object.entries(value).map(([key, item]) => [convert(key), convertKeys(item, convert)]),
  )
}

// OnWork REST API 클라이언트. Base: /api/v1 (ADR-SYS-002: Bearer JWT)
export const api = axios.create({
  baseURL: import.meta.env.VITE_API_BASE ?? 'http://localhost:8080/api/v1',
  headers: { 'Content-Type': 'application/json' },
})

// Access Token 자동 첨부 (Phase 1에서 로그인 시 저장)
api.interceptors.request.use((config) => {
  const token = localStorage.getItem('onwork.accessToken')
  if (token) config.headers.Authorization = `Bearer ${token}`
  if (config.data) config.data = convertKeys(config.data, toSnake)
  if (config.params) config.params = convertKeys(config.params, toSnake)
  return config
})

api.interceptors.response.use(
  (response) => {
    response.data = convertKeys(response.data, toCamel)
    return response
  },
  (error) => {
    if (error.response?.data) error.response.data = convertKeys(error.response.data, toCamel)
    return Promise.reject(error)
  },
)
