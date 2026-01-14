import { request } from '@/utils/request'
import type { ApiResponse, PageParams, PageResult, Product, Category } from '@/types'

/**
 * 获取商品列表
 */
export function getProductList(params: PageParams & { keyword?: string; categoryId?: number; status?: number }) {
  return request.get<ApiResponse<PageResult<Product>>>('/admin/products', { params })
}

/**
 * 获取商品详情
 */
export function getProductDetail(id: number) {
  return request.get<ApiResponse<Product>>(`/admin/products/${id}`)
}

/**
 * 创建商品
 */
export function createProduct(data: Partial<Product>) {
  return request.post<ApiResponse>('/admin/products', data)
}

/**
 * 更新商品
 */
export function updateProduct(id: number, data: Partial<Product>) {
  return request.put<ApiResponse>(`/admin/products/${id}`, data)
}

/**
 * 删除商品
 */
export function deleteProduct(id: number) {
  return request.delete<ApiResponse>(`/admin/products/${id}`)
}

/**
 * 更新商品状态
 */
export function updateProductStatus(id: number, status: number) {
  return request.put<ApiResponse>(`/admin/products/${id}/status`, { status })
}

/**
 * 设置商品推荐
 */
export function updateProductRecommend(id: number, isRecommend: number) {
  return request.put<ApiResponse>(`/admin/products/${id}/recommend`, { isRecommend })
}

/**
 * 修改商品销量
 */
export function updateProductSales(id: number, salesCount: number) {
  return request.put<ApiResponse>(`/admin/products/${id}/sales`, { salesCount })
}

/**
 * 获取分类列表
 */
export function getCategoryList() {
  return request.get<ApiResponse<Category[]>>('/admin/categories')
}

/**
 * 创建分类
 */
export function createCategory(data: Partial<Category>) {
  return request.post<ApiResponse>('/admin/categories', data)
}

/**
 * 更新分类
 */
export function updateCategory(id: number, data: Partial<Category>) {
  return request.put<ApiResponse>(`/admin/categories/${id}`, data)
}

/**
 * 删除分类
 */
export function deleteCategory(id: number) {
  return request.delete<ApiResponse>(`/admin/categories/${id}`)
}

/**
 * 上传图片
 */
export function uploadImage(file: File) {
  return request.upload<ApiResponse<{ url: string }>>('/admin/upload/image', file)
}

// ==================== 商品规格管理 ====================

export interface ProductSpec {
  id?: number
  productId: number
  specName: string
  price: number
  vipPrice?: number
  stock: number
  sortOrder?: number
  status?: number
}

/**
 * 获取商品规格列表
 */
export function getProductSpecs(productId: number) {
  return request.get<ApiResponse<ProductSpec[]>>(`/admin/products/${productId}/specs`)
}

/**
 * 添加商品规格
 */
export function addProductSpec(productId: number, data: Partial<ProductSpec>) {
  return request.post<ApiResponse<ProductSpec>>(`/admin/products/${productId}/specs`, data)
}

/**
 * 更新商品规格
 */
export function updateProductSpec(specId: number, data: Partial<ProductSpec>) {
  return request.put<ApiResponse<ProductSpec>>(`/admin/products/specs/${specId}`, data)
}

/**
 * 删除商品规格
 */
export function deleteProductSpec(specId: number) {
  return request.delete<ApiResponse>(`/admin/products/specs/${specId}`)
}