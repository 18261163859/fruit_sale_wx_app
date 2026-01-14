// 通用响应类型
export interface ApiResponse<T = any> {
  code: number
  message: string
  data: T
}

// 分页参数
export interface PageParams {
  page: number
  pageSize: number
}

// 分页响应
export interface PageResult<T> {
  records: T[]
  list?: T[]  // 兼容字段
  total: number
  current: number
  size: number
  page?: number  // 兼容字段
  pageSize?: number  // 兼容字段
  pages?: number
}

// 用户类型
export interface User {
  id: number
  username: string
  nickname: string
  avatar?: string
  phone: string
  email?: string
  vipLevel: number
  vipExpireTime?: string
  points: number
  balance: number
  agentLevel: number
  inviteCode: string
  parentId?: number
  status: number
  isShipper?: number  // 是否为发货人员：0-否，1-是
  createdAt: string
  updatedAt: string
}

// 代理申请
export interface AgentApplication {
  id: number
  userId: number
  username: string
  phone: string
  realName: string
  idCard: string
  idCardFront: string
  idCardBack: string
  applyLevel: number
  status: number // 0-待审核 1-通过 2-拒绝
  rejectReason?: string
  createdAt: string
  updatedAt: string
}

// 商品类型
export interface Product {
  id: number
  name: string
  categoryId: number
  categoryName?: string
  cover: string
  images: string[]
  price: number
  vipPrice: number
  agentPrice: number
  stock: number
  sales: number
  description: string
  detail: string
  status: number // 0-下架 1-上架
  sort: number
  createdAt: string
  updatedAt: string
}

// 商品分类
export interface Category {
  id: number
  name: string
  icon?: string
  sort: number
  status: number
  createdAt: string
}

// 订单类型
export interface Order {
  id: number
  orderNo: string
  userId: number
  username: string
  phone: string
  productId: number
  productName: string
  productCover: string
  price: number
  quantity: number
  totalAmount: number
  receiverName: string
  receiverPhone: string
  receiverAddress: string
  expressCompany?: string
  expressNo?: string
  status: number // 0-待付款 1-待发货 2-待收货 3-已完成 4-已取消
  payTime?: string
  shipTime?: string
  finishTime?: string
  cancelTime?: string
  remark?: string
  createdAt: string
  updatedAt: string
}

// 轮播图
export interface Banner {
  id: number
  title: string
  image: string
  link?: string
  sort: number
  status: number
  createdAt: string
}

// 系统配置
export interface SystemConfig {
  id: number
  configKey: string
  configValue: string
  configType: string
  description?: string
  createdAt: string
  updatedAt: string
}

// 积分卡
export interface PointCard {
  id: number
  code: string
  points: number
  status: number // 0-未使用 1-已使用
  userId?: number
  username?: string
  useTime?: string
  createdAt: string
}

// 财务记录
export interface FinanceRecord {
  id: number
  userId: number
  username: string
  type: number // 1-充值 2-消费 3-返现 4-提现
  amount: number
  balance: number
  orderNo?: string
  remark?: string
  createdAt: string
}

// 统计数据
export interface Statistics {
  totalOrders: number
  totalSales: number
  totalUsers: number
  totalAgents: number
  todayOrders: number
  todaySales: number
  todayUsers: number
}

// 登录表单
export interface LoginForm {
  username: string
  password: string
  remember: boolean
}

// 管理员信息
export interface AdminInfo {
  id: number
  username: string
  nickname: string
  avatar?: string
  roles: string[]
  permissions: string[]
}