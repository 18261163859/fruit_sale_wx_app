import { request } from '@/utils/request'
import type { ApiResponse, Statistics } from '@/types'

/**
 * 获取统计数据
 */
export function getStatistics() {
  return request.get<ApiResponse<Statistics>>('/admin/dashboard/statistics')
}

/**
 * 获取销售趋势数据
 */
export function getSalesTrend(days: number = 7) {
  return request.get<ApiResponse<{ date: string; amount: number; count: number }[]>>(
    '/admin/dashboard/sales-trend',
    { params: { days } }
  )
}

/**
 * 获取最近订单
 */
export function getRecentOrders(limit: number = 10) {
  return request.get<ApiResponse<any[]>>('/admin/dashboard/recent-orders', { params: { limit } })
}