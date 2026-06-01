#!/usr/bin/env node

const BASE_URL = process.env.ONWORK_BASE_URL ?? 'http://localhost:8080/api/v1'
const EMAIL = process.env.ONWORK_EMAIL ?? 'hyunjun@onwork.kr'
const PASSWORD = process.env.ONWORK_PASSWORD ?? 'onwork1234!'
const CONCURRENCY = Number(process.env.ONWORK_LOAD_CONCURRENCY ?? 8)
const DURATION_MS = Number(process.env.ONWORK_LOAD_DURATION_SEC ?? 30) * 1000

const targets = [
  { name: 'login', method: 'POST', path: '/auth/login', body: () => ({ email: EMAIL, password: PASSWORD }), auth: false },
  { name: 'approvals', method: 'GET', path: '/approvals', auth: true },
  { name: 'employees', method: 'GET', path: '/hr/employees', auth: true },
  { name: 'attendance_anomalies', method: 'GET', path: '/attendance/anomalies', auth: true },
]

const stats = new Map(targets.map((target) => [target.name, { latencies: [], errors: 0, count: 0 }]))

function percentile(values, p) {
  if (values.length === 0) return 0
  const sorted = [...values].sort((a, b) => a - b)
  const index = Math.min(sorted.length - 1, Math.ceil((p / 100) * sorted.length) - 1)
  return Math.round(sorted[index])
}

async function request(target, token) {
  const start = performance.now()
  const headers = { 'Content-Type': 'application/json' }
  if (target.auth && token) headers.Authorization = `Bearer ${token}`
  const response = await fetch(`${BASE_URL}${target.path}`, {
    method: target.method,
    headers,
    body: target.body ? JSON.stringify(target.body()) : undefined,
  })
  const elapsed = performance.now() - start
  const bucket = stats.get(target.name)
  bucket.count += 1
  bucket.latencies.push(elapsed)
  if (!response.ok) {
    bucket.errors += 1
    await response.text().catch(() => '')
    return null
  }
  return response.json().catch(() => null)
}

async function login() {
  const data = await request(targets[0], null)
  return data?.access_token ?? data?.accessToken
}

async function worker(stopAt) {
  let token = await login()
  while (Date.now() < stopAt) {
    for (const target of targets) {
      if (Date.now() >= stopAt) break
      if (target.auth && !token) token = await login()
      const data = await request(target, token)
      if (target.name === 'login' && data?.access_token) token = data.access_token
      if (target.name === 'login' && data?.accessToken) token = data.accessToken
    }
  }
}

const stopAt = Date.now() + DURATION_MS
await Promise.all(Array.from({ length: CONCURRENCY }, () => worker(stopAt)))

console.log(`base_url=${BASE_URL} concurrency=${CONCURRENCY} duration_sec=${DURATION_MS / 1000}`)
for (const [name, bucket] of stats.entries()) {
  const p95 = percentile(bucket.latencies, 95)
  const p99 = percentile(bucket.latencies, 99)
  const avg = bucket.latencies.length
    ? Math.round(bucket.latencies.reduce((sum, value) => sum + value, 0) / bucket.latencies.length)
    : 0
  const errorRate = bucket.count ? ((bucket.errors / bucket.count) * 100).toFixed(2) : '0.00'
  console.log(`${name}: count=${bucket.count} errors=${bucket.errors} error_rate=${errorRate}% avg_ms=${avg} p95_ms=${p95} p99_ms=${p99}`)
}
