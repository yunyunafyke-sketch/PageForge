import http from './http'
import type { LoginRequest, LoginResult, RegisterRequest } from '@/types/api'

export const login = (data: LoginRequest) => http.post<LoginResult>('/auth/login', data)

export const register = (data: RegisterRequest) => http.post<number>('/auth/register', data)

export const changePassword = (data: { oldPassword: string; newPassword: string }) =>
  http.post<boolean>('/auth/change-password', data)
