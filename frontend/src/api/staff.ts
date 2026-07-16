import http from './http'
import type { OutsourcedStaff, PageResult } from '@/types/api'

const base = '/admin/outsourced-staff'
export type StaffForm = Pick<OutsourcedStaff, 'name' | 'accountId' | 'phone' | 'status'>

export const pageStaff = (data: { pageNum: number; pageSize: number; name?: string; status?: number }) =>
  http.post<PageResult<OutsourcedStaff>>(`${base}/page`, data)

export const getStaffDetail = (id: number) =>
  http.post<OutsourcedStaff>(`${base}/detail`, { id })

export const createStaff = (data: StaffForm) => http.post<number>(`${base}/create`, data)

export const updateStaff = (data: StaffForm & { id: number }) =>
  http.post<boolean>(`${base}/update`, data)

export const deleteStaff = (id: number) => http.post<boolean>(`${base}/delete`, { id })

export const batchDeleteStaff = (ids: number[]) =>
  http.post<boolean>(`${base}/batch-delete`, { ids })

export const changeStaffStatus = (id: number, status: number) =>
  http.post<boolean>(`${base}/change-status`, { id, status })
