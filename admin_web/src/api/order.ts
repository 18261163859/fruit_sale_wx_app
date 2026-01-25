import { request } from '@/utils/request'
import type { ApiResponse, PageParams, PageResult, Order } from '@/types'

/**
 * 获取订单列表
 */
export function getOrderList(params: PageParams & { keyword?: string; status?: number; startDate?: string; endDate?: string }) {
  return request.get<ApiResponse<PageResult<Order>>>('/admin/orders', { params })
}

/**
 * 获取订单详情
 */
export function getOrderDetail(id: number) {
  return request.get<ApiResponse<Order>>(`/admin/orders/${id}`)
}

/**
 * 发货
 */
export function shipOrder(id: number, data: { expressCompany: string; expressNo: string }) {
  return request.post<ApiResponse>(`/admin/orders/${id}/ship`, data)
}

/**
 * 确认完成
 */
export function finishOrder(id: number) {
  return request.post<ApiResponse>(`/admin/orders/${id}/finish`)
}

/**
 * 撤回发货
 */
export function cancelShipment(id: number) {
  return request.post<ApiResponse>(`/admin/orders/${id}/cancel-shipment`)
}

/**
 * 导出订单
 */
export function exportOrders(params: { status?: number; startDate?: string; endDate?: string }) {
  return request.get('/admin/orders/export', {
    params,
    responseType: 'blob'
  })
}