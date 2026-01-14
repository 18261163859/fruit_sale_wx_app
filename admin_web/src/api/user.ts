import { request } from '@/utils/request'
import type { ApiResponse, PageParams, PageResult, User } from '@/types'

/**
 * 获取用户列表
 */
export function getUserList(params: PageParams & { keyword?: string; agentLevel?: number; vipLevel?: number }) {
  return request.get<ApiResponse<PageResult<User>>>('/admin/users', { params })
}

/**
 * 获取用户详情
 */
export function getUserDetail(id: number) {
  return request.get<ApiResponse<User>>(`/admin/users/${id}`)
}

/**
 * 设置一级代理
 */
export function setFirstAgent(userId: number) {
  return request.post<ApiResponse>(`/admin/users/${userId}/set-agent`)
}

/**
 * 更新用户状态
 */
export function updateUserStatus(userId: number, status: number) {
  return request.put<ApiResponse>(`/admin/users/${userId}/status`, { status })
}

/**
 * 更新用户信息
 */
export function updateUser(userId: number, data: any) {
  return request.put<ApiResponse>(`/admin/users/${userId}`, data)
}

/**
 * 设置/取消用户为发货人员
 */
export function setUserShipper(userId: number, isShipper: number) {
  return request.put<ApiResponse>(`/admin/users/${userId}/shipper`, { isShipper })
}