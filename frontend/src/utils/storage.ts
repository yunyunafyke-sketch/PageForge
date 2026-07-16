import type { LoginResult } from '@/types/api'

const AUTH_KEY = 'pageforge_auth'

export function loadAuth(): LoginResult | null {
  try {
    const value = localStorage.getItem(AUTH_KEY)
    return value ? (JSON.parse(value) as LoginResult) : null
  } catch {
    localStorage.removeItem(AUTH_KEY)
    return null
  }
}

export function saveAuth(auth: LoginResult): void {
  localStorage.setItem(AUTH_KEY, JSON.stringify(auth))
}

export function clearAuth(): void {
  localStorage.removeItem(AUTH_KEY)
}
