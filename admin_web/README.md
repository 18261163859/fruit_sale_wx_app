# 水果商城后台管理系统

基于 Vue3 + TypeScript + Vite + Element Plus 构建的现代化后台管理系统

## 技术栈

- **框架**: Vue 3.5 + TypeScript
- **构建工具**: Vite 7.x
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **路由管理**: Vue Router 4.x
- **HTTP客户端**: Axios
- **图表库**: ECharts
- **工具库**: VueUse

## 功能模块

### 1. 登录模块
- 管理员账号密码登录
- 记住密码功能
- Token认证管理

### 2. 首页Dashboard
- 数据统计卡片（总订单数、总销售额、总用户数、总代理数）
- 销售趋势图表（支持7天/30天切换）
- 最近订单列表

### 3. 用户管理
- 用户列表（支持分页、搜索、筛选）
- 设置一级代理
- 查看用户详情
- 用户状态管理（启用/禁用）

### 4. 代理管理
- 代理申请审批列表
- 审批操作（通过/拒绝，支持填写拒绝理由）
- 代理列表查询
- 代理等级筛选

### 5. 商品管理
- 商品列表（分页、搜索、分类筛选）
- 添加/编辑商品
- 商品图片上传
- 上下架操作
- 商品分类管理

### 6. 订单管理
- 订单列表（状态筛选、日期范围筛选）
- 订单详情查看
- 订单发货（填写物流公司和单号）
- 确认完成订单
- 订单导出（Excel）

### 7. 轮播图管理
- 轮播图列表
- 添加/编辑轮播图
- 图片上传
- 排序管理
- 启用/禁用状态

### 8. 系统配置
- 基础配置（VIP价格、折扣、运费、包邮金额）
- 代理配置（代理折扣、返现比例）
- 积分配置（积分比例、抵扣规则）
- 联系方式配置
- 网站基本信息配置

### 9. 积分卡管理
- 批量生成积分卡
- 积分卡列表查询
- 使用状态筛选
- 导出兑换码（Excel）

### 10. 财务管理
- 财务统计（总收入、总返现、总提现）
- 订单流水列表
- 交易类型筛选
- 日期范围筛选
- 财务报表导出（Excel）

## 核心特性

### 1. 自动化配置
- Element Plus 组件自动导入
- Vue API 自动导入
- 路径别名 @ 指向 src

### 2. 响应式布局
- 侧边栏可折叠
- 顶部导航栏
- 主内容区域自适应

### 3. 深色模式
- 支持深色/浅色主题切换
- 主题状态持久化
- 平滑过渡动画

### 4. 权限控制
- 路由守卫（未登录自动跳转登录页）
- Token认证
- 登录状态持久化

### 5. 请求处理
- 统一的请求拦截器
- 统一的响应处理
- 自动Token注入
- 全局错误提示

### 6. 用户体验
- Loading加载状态
- 操作成功/失败提示
- 确认对话框
- 图片预览
- 表格分页
- 搜索筛选

## 项目结构

```
admin_web/
├── public/                 # 静态资源
├── src/
│   ├── api/               # API接口定义
│   │   ├── auth.ts        # 认证接口
│   │   ├── dashboard.ts   # 首页接口
│   │   ├── user.ts        # 用户接口
│   │   ├── agent.ts       # 代理接口
│   │   ├── product.ts     # 商品接口
│   │   ├── order.ts       # 订单接口
│   │   └── other.ts       # 其他接口
│   ├── assets/            # 静态资源
│   ├── components/        # 公共组件
│   ├── layouts/           # 布局组件
│   │   └── index.vue      # 主布局
│   ├── router/            # 路由配置
│   │   └── index.ts       # 路由定义
│   ├── stores/            # Pinia状态管理
│   │   ├── user.ts        # 用户状态
│   │   └── app.ts         # 应用状态
│   ├── styles/            # 全局样式
│   │   └── index.css      # 主样式文件
│   ├── types/             # TypeScript类型定义
│   │   └── index.ts       # 类型定义
│   ├── utils/             # 工具函数
│   │   ├── request.ts     # Axios封装
│   │   └── index.ts       # 通用工具
│   ├── views/             # 页面组件
│   │   ├── login/         # 登录页
│   │   ├── dashboard/     # 首页
│   │   ├── user/          # 用户管理
│   │   ├── agent/         # 代理管理
│   │   ├── product/       # 商品管理
│   │   ├── order/         # 订单管理
│   │   ├── banner/        # 轮播图管理
│   │   ├── system/        # 系统配置
│   │   ├── point-card/    # 积分卡管理
│   │   └── finance/       # 财务管理
│   ├── App.vue            # 根组件
│   └── main.ts            # 入口文件
├── index.html             # HTML模板
├── vite.config.ts         # Vite配置
├── tsconfig.json          # TypeScript配置
└── package.json           # 项目依赖
```

## 安装运行

### 安装依赖
```bash
npm install
```

### 开发模式
```bash
npm run dev
```

访问 http://localhost:5173

### 生产构建
```bash
npm run build
```

### 预览构建
```bash
npm run preview
```

## 环境要求

- Node.js >= 18.x
- npm >= 9.x

## 后端API

默认后端API地址：http://localhost:8080

可在 `vite.config.ts` 中修改代理配置。

## 浏览器支持

- Chrome >= 90
- Firefox >= 88
- Safari >= 14
- Edge >= 90

## 注意事项

1. 本项目使用 Vite 构建，开发时热更新速度快
2. Element Plus 组件已配置自动导入，无需手动引入
3. 使用 TypeScript 开发，享受类型检查带来的便利
4. 所有API请求都会自动添加Token认证
5. 图片上传功能需要后端支持对应的上传接口
6. 导出功能需要后端返回Blob数据

## 开发建议

1. 遵循 Vue3 Composition API 写法
2. 使用 TypeScript 进行类型约束
3. 保持代码风格统一
4. 组件命名使用 PascalCase
5. 文件命名使用 kebab-case

## 许可证

MIT License