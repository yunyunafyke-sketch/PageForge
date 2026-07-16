import axios, { AxiosError, type AxiosRequestConfig } from 'axios'
import { ElMessage } from 'element-plus'
import router from '@/router'
import { clearAuth, loadAuth } from '@/utils/storage'
import type { ResultModel } from '@/types/api'

const http = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL || '/api',
  timeout: 15000,
})

http.interceptors.request.use((config) => {
  const token = loadAuth()?.token
  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }
  return config
})

http.interceptors.response.use(
  (response) => response,
  (error: AxiosError<ResultModel<unknown>>) => {
    const status = error.response?.status
    const message = error.response?.data?.errorMessage || error.message || '网络请求失败'
    if (status === 401) {
      clearAuth()
      void router.replace({ name: 'login', query: { redirect: router.currentRoute.value.fullPath } })
    }
    ElMessage.error(message)
    return Promise.reject(error)
  },
)

const api = {
  async post<T>(url: string, data?: unknown, config?: AxiosRequestConfig): Promise<T> {
    const response = await http.post<ResultModel<T>>(url, data, config)
    const result = response.data
    if (!result.success) {
      const message = result.errorMessage || '请求失败'
      ElMessage.error(message)
      throw new Error(message)
    }
    return result.data
  },
}

export default api
