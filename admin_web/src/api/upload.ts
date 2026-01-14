import { request } from '@/utils/request'
import type { ApiResponse } from '@/types'

/**
 * 上传用户头像
 */
export function uploadUserAvatar(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<ApiResponse<{ url: string }>>('/upload/user/avatar', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传商品图片
 */
export function uploadProductImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<ApiResponse<{ url: string }>>('/upload/product/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传系统图片（轮播图等）
 */
export function uploadSystemImage(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<ApiResponse<{ url: string }>>('/upload/system/image', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

/**
 * 上传系统视频（轮播图视频等）
 */
export function uploadSystemVideo(file: File) {
  const formData = new FormData()
  formData.append('file', file)
  return request.post<ApiResponse<{ url: string }>>('/upload/system/video', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}
