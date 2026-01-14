import { request } from '@/utils/request'
import type { ApiResponse, PageParams, PageResult, AgentApplication } from '@/types'

/**
 * 获取代理申请列表
 */
export function getAgentApplications(params: PageParams & { status?: number }) {
  return request.get<ApiResponse<PageResult<AgentApplication>>>('/admin/agent/applications', { params })
}

/**
 * 审批代理申请
 */
export function approveAgent(id: number, status: number, rejectReason?: string) {
  return request.post<ApiResponse>(`/admin/agent/applications/${id}/approve`, { status, rejectReason })
}

/**
 * 获取代理列表
 */
export function getAgentList(params: PageParams & { keyword?: string; level?: number }) {
  return request.get<ApiResponse<PageResult<any>>>('/admin/agents', { params })
}

/**
 * 获取代理收益统计
 */
export function getAgentEarnings(agentId: number, params: { startDate?: string; endDate?: string }) {
  return request.get<ApiResponse<any>>(`/admin/agents/${agentId}/earnings`, { params })
}

/**
 * 获取返现记录列表
 */
export function getCommissionRecords(params: PageParams & { agentId?: number; startDate?: string; endDate?: string }) {
  return request.get<ApiResponse<PageResult<any>>>('/admin/commission/records', { params })
}

/**
 * 获取返现统计
 */
export function getCommissionStatistics(params?: { startDate?: string; endDate?: string }) {
  return request.get<ApiResponse<any>>('/admin/commission/statistics', { params })
}
/**
 * 获取邀请二级代理申请列表
 */
export function getInviteApplications(status?: number) {
  return request.get<ApiResponse<any[]>>('/admin/agent/invite-applications', {
    params: status !== undefined ? { status } : {}
  })
}

/**
 * 审核邀请二级代理申请
 */
export function reviewInviteApplication(data: { applicationId: number; status: number; rejectReason?: string }) {
  return request.post<ApiResponse>('/admin/agent/invite-applications/review', data)
}

/**
 * 获取返现申请列表
 */
export function getCommissionApplications(status?: number) {
  return request.get<ApiResponse<any[]>>('/admin/commission-applications', {
    params: status !== undefined ? { status } : {}
  })
}

/**
 * 审核返现申请
 */
export function reviewCommissionApplication(data: { applicationId: number; status: number; rejectReason?: string }) {
  return request.post<ApiResponse>('/admin/commission-applications/review', data)
}

/**
 * 标记返现申请为已返现
 */
export function markAsTransferred(applicationId: number) {
  return request.post<ApiResponse>(`/admin/commission-applications/${applicationId}/transfer`)
}
