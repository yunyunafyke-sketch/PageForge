import http from './http'
import type { FunctionItem, PageResult, Role, SystemUser } from '@/types/api'

const base = '/admin/system'

export const pageUsers = (data: { pageNum: number; pageSize: number; keyword?: string }) =>
  http.post<PageResult<SystemUser>>(`${base}/user/page`, data)

export const assignUserRoles = (data: { userId: number; roleIds: number[] }) =>
  http.post<boolean>(`${base}/user/assign-roles`, data)

export const resetUserPassword = (userId: number) =>
  http.post<boolean>(`${base}/user/reset-password`, { userId })

export const pageRoles = (data: { pageNum: number; pageSize: number; keyword?: string }) =>
  http.post<PageResult<Role>>(`${base}/role/page`, data)

export const createRole = (data: Omit<Role, 'id' | 'functionIds'>) =>
  http.post<number>(`${base}/role/create`, data)

export const updateRole = (data: Omit<Role, 'functionIds'>) =>
  http.post<boolean>(`${base}/role/update`, data)

export const assignRoleFunctions = (data: { roleId: number; functionIds: number[] }) =>
  http.post<boolean>(`${base}/role/assign-functions`, data)

export const pageFunctions = (data: { pageNum: number; pageSize: number; keyword?: string }) =>
  http.post<PageResult<FunctionItem>>(`${base}/function/page`, data)

export const createFunction = (data: Omit<FunctionItem, 'id'>) =>
  http.post<number>(`${base}/function/create`, data)

export const updateFunction = (data: FunctionItem) =>
  http.post<boolean>(`${base}/function/update`, data)
