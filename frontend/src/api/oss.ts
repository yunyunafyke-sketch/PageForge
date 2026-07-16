import http from './http'
import type { OssFile } from '@/types/api'

export const uploadFile = (file: File, directory: string, onProgress?: (percent: number) => void) => {
  const data = new FormData()
  data.append('file', file)
  if (directory) data.append('directory', directory)
  return http.post<OssFile>('/user/oss/upload', data, {
    headers: { 'Content-Type': 'multipart/form-data' },
    onUploadProgress: (event) => {
      if (event.total) onProgress?.(Math.round((event.loaded * 100) / event.total))
    },
  })
}

export const deleteFile = (objectKey: string) =>
  http.post<boolean>('/admin/oss/delete', { objectKey })
