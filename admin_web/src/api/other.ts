import { request } from '@/utils/request'
import type { ApiResponse, Banner, SystemConfig, PointCard, FinanceRecord, PageParams, PageResult } from '@/types'

// ===== 轮播图 =====
export function getBannerList() {
  return request.get<ApiResponse<Banner[]>>('/admin/banners')
}

export function createBanner(data: Partial<Banner>) {
  return request.post<ApiResponse>('/admin/banners', data)
}

export function updateBanner(id: number, data: Partial<Banner>) {
  return request.put<ApiResponse>(`/admin/banners/${id}`, data)
}

export function deleteBanner(id: number) {
  return request.delete<ApiResponse>(`/admin/banners/${id}`)
}

// ===== 系统配置 =====
export function getSystemConfig() {
  return request.get<ApiResponse<SystemConfig[]>>('/admin/config/list')
}

export function updateSystemConfig(data: Record<string, any>) {
  return request.put<ApiResponse>('/admin/config/update', data)
}

// ===== 积分卡 =====
export function getPointCardList(params: PageParams & { keyword?: string; status?: number }) {
  return request.get<ApiResponse<PageResult<PointCard>>>('/admin/point-cards', { params })
}

export function generatePointCards(data: { count: number; points: number }) {
  return request.post<ApiResponse>('/admin/point-cards/generate', {
    count: data.count,
    integralAmount: data.points
  })
}

export function exportPointCards(params: { status?: number }) {
  return request.get('/admin/point-cards/export', {
    params,
    responseType: 'blob'
  })
}

// ===== 财务管理 =====
export function getFinanceRecords(params: PageParams & { type?: number; startDate?: string; endDate?: string }) {
  return request.get<ApiResponse<PageResult<FinanceRecord>>>('/admin/finance/records', { params })
}

export function getFinanceStatistics(params: { startDate?: string; endDate?: string }) {
  return request.get<ApiResponse<any>>('/admin/finance/statistics', { params })
}

export function exportFinanceRecords(params: { type?: number; startDate?: string; endDate?: string }) {
  return request.get('/admin/finance/export', {
    params,
    responseType: 'blob'
  })
}