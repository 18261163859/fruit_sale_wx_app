import { request } from '@/utils/request'
import type { ApiResponse, LoginForm, AdminInfo } from '@/types'

/**
 * 登录
 */
export function login(data: LoginForm) {
  return request.post<ApiResponse<{ token: string; userInfo: AdminInfo }>>('/auth/admin/login', data)
}

/**
 * 获取管理员信息
 */
export function getAdminInfo() {
  return request.get<ApiResponse<AdminInfo>>('/auth/admin/info')
}

/**
 * 退出登录
 */
export function logout() {
  return request.post<ApiResponse>('/auth/admin/logout')
}