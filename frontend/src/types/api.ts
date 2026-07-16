export interface ResultModel<T> {
  status: number
  success: boolean
  errorCode?: string
  errorMessage?: string
  data: T
}

export interface PageRequest {
  pageNum: number
  pageSize: number
}

export interface PageResult<T> {
  total: number
  records: T[]
  pageNum: number
  pageSize: number
}

export interface LoginRequest {
  username: string
  password: string
}

export interface LoginResult {
  token: string
  userId: number
  username: string
  roles: string[]
  functions: string[]
}

export interface RegisterRequest {
  username: string
  password: string
  nickname: string
}

export interface SystemUser {
  id: number
  username: string
  nickname: string
  status: number
  roleIds: number[]
}

export interface Role {
  id: number
  roleCode: string
  roleName: string
  description?: string
  functionIds: number[]
}

export interface FunctionItem {
  id: number
  functionCode: string
  functionName: string
  description?: string
}

export interface OutsourcedStaff {
  id: number
  name: string
  accountId: string
  phone?: string
  status: number
  gmtCreate: string
  gmtModified: string
}

export interface OssFile {
  originalName: string
  objectKey: string
  url: string
  size: number
}
