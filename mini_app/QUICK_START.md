# 高端云南水果小程序 - 快速开始指南

## 项目简介

这是一个功能完善的微信小程序电商项目,包含完整的购物流程、代理系统和积分系统。

**当前完成度**: 约45% (核心架构和基础功能已完成)

---

## 开发环境要求

- **微信开发者工具**: 最新稳定版
- **Node.js**: 12.x 或更高版本(可选,用于包管理)
- **操作系统**: Windows/MacOS/Linux

---

## 快速开始

### 1. 打开项目

使用微信开发者工具打开项目:

```
项目路径: /Users/shengmingxing/fruit_sale/mini_app
```

### 2. 配置AppID

在 `project.config.json` 中配置你的AppID:

```json
{
  "appid": "你的AppID"
}
```

### 3. 配置后端地址

在 `utils/request.js` 中修改后端API地址:

```javascript
const BASE_URL = 'http://localhost:8080'; // 改为你的后端地址
```

### 4. 准备图片资源

将以下图片放入 `images/` 目录:

- `logo.png` - Logo图片
- `tab/home.png` 和 `tab/home-active.png` - 首页图标
- `tab/category.png` 和 `tab/category-active.png` - 分类图标
- `tab/cart.png` 和 `tab/cart-active.png` - 购物车图标
- `tab/profile.png` 和 `tab/profile-active.png` - 我的图标

### 5. 运行项目

在微信开发者工具中点击"编译"按钮即可运行。

---

## 已实现功能

### ✅ 核心架构
- HTTP请求封装(自动Token认证、统一错误处理)
- 主题切换系统(三种主题自动切换)
- 本地存储管理
- 完整的工具函数库

### ✅ API接口
- 用户模块(登录、信息获取)
- 商品模块(列表、详情、搜索)
- 购物车模块(增删改查)
- 订单模块(创建、查询、支付)
- 地址模块(完整的地址管理)
- 代理模块(代理信息、返现记录)
- 积分模块(余额、记录、兑换)
- VIP模块(开通、权益)

### ✅ 已完成页面
1. **登录页面** - 微信授权登录,获取手机号
2. **首页** - 轮播图、推荐商品、搜索
3. **购物车** - 选中、数量调整、删除、结算

---

## 主题系统

### 三种主题配色

#### 🔵 普通会员
- 主色调: 蓝色(#1989fa)
- 背景色: 浅灰(#f5f5f5)
- 适用: 普通注册用户

#### 👑 星享会员
- 主色调: 金色(#d4af37)
- 背景色: 深黑(#0a0a0a)
- 适用: VIP会员和代理

#### 🎊 新年特供
- 主色调: 红色(#ff4444)
- 辅助色: 金色(#d4af37)
- 适用: 所有用户(农历腊月30至正月15)

### 主题切换逻辑

系统会自动根据用户身份和当前日期选择合适的主题:

```javascript
// 1. 检查是否在新年期间
if (isFestivalPeriod()) {
  return 'festival'; // 红金配色
}

// 2. 检查用户身份
if (userInfo.memberType === 'vip' || userInfo.memberType === 'agent1' || userInfo.memberType === 'agent2') {
  return 'vip'; // 黑金配色
}

// 3. 默认普通会员
return 'normal'; // 蓝白配色
```

---

## 目录结构说明

```
mini_app/
├── api/              # API接口封装(8个模块)
├── utils/            # 工具函数(6个模块)
├── pages/            # 页面文件(18个页面)
├── components/       # 公共组件(待开发)
├── styles/           # 全局样式(待完善)
├── images/           # 图片资源(待准备)
├── app.js            # 应用入口
├── app.json          # 应用配置
└── app.wxss          # 全局样式
```

---

## 开发指南

### 创建新页面

参考 `PAGE_TEMPLATE.md` 文档,每个页面需要4个文件:

1. **page-name.js** - 页面逻辑
2. **page-name.wxml** - 页面结构
3. **page-name.wxss** - 页面样式
4. **page-name.json** - 页面配置

### 页面模板

```javascript
// pages/page-name/page-name.js
const { applyTheme } = require('../../utils/theme.js');
const { getUserInfo } = require('../../utils/storage.js');

Page({
  data: {
    theme: 'normal',
    themeStyle: {}
  },

  onLoad() {
    this.initTheme();
    this.loadData();
  },

  initTheme() {
    const userInfo = getUserInfo();
    applyTheme(this, userInfo);
  },

  async loadData() {
    // 加载数据
  }
});
```

### API调用示例

```javascript
// 导入API
const { getProducts } = require('../../api/product.js');

// 调用接口
async loadProducts() {
  try {
    const res = await getProducts({ page: 1, pageSize: 20 });
    if (res.success) {
      this.setData({
        products: res.data.list || []
      });
    }
  } catch (err) {
    console.error('加载失败:', err);
  }
}
```

### 主题样式使用

```xml
<!-- 在WXML中使用主题样式 -->
<view style="background-color: {{themeStyle.backgroundColor}}">
  <text style="color: {{themeStyle.textColor}}">文本</text>
  <button style="background-color: {{themeStyle.buttonColor}}">按钮</button>
</view>
```

---

## 后端接口说明

### 基础地址
```
http://localhost:8080
```

### 认证方式
所有需要登录的接口都需要在请求头中携带Token:

```
Authorization: Bearer {token}
```

### 响应格式
统一的响应格式:

```json
{
  "code": 0,
  "success": true,
  "message": "success",
  "data": {}
}
```

### 主要接口

#### 用户相关
- `POST /api/auth/wx-login` - 微信登录
- `GET /api/user/info` - 获取用户信息

#### 商品相关
- `GET /api/products` - 获取商品列表
- `GET /api/products/:id` - 获取商品详情
- `GET /api/banners` - 获取轮播图

#### 购物车相关
- `GET /api/cart` - 获取购物车
- `POST /api/cart` - 添加到购物车
- `PUT /api/cart/:id` - 更新数量
- `DELETE /api/cart/:id` - 删除商品

---

## 待完成功能

### 优先级 P0 (必须完成)
- [ ] 分类页面
- [ ] 商品详情页面
- [ ] 结算页面
- [ ] 我的页面
- [ ] 订单列表
- [ ] 订单详情
- [ ] 微信支付集成

### 优先级 P1 (重要)
- [ ] 搜索页面
- [ ] 地址管理
- [ ] 公共组件开发
- [ ] 图片资源准备

### 优先级 P2 (可选)
- [ ] 代理功能页面
- [ ] 积分功能页面
- [ ] 分享功能
- [ ] 样式优化

---

## 常见问题

### Q: 如何切换主题?
A: 主题会根据用户身份和当前日期自动切换,无需手动操作。

### Q: 如何调用API?
A: 使用已封装的API模块,例如:
```javascript
const { getProducts } = require('../../api/product.js');
const res = await getProducts({ page: 1 });
```

### Q: 如何处理登录状态?
A: 使用 `utils/storage.js` 中的 `isLogin()` 方法判断:
```javascript
const { isLogin } = require('../../utils/storage.js');
if (!isLogin()) {
  // 跳转到登录页
}
```

### Q: 页面不显示怎么办?
A: 检查以下几点:
1. 页面是否在 `app.json` 中注册
2. 文件路径是否正确
3. 文件名是否一致

---

## 开发工具和资源

### 官方文档
- [微信小程序官方文档](https://developers.weixin.qq.com/miniprogram/dev/framework/)
- [微信小程序API文档](https://developers.weixin.qq.com/miniprogram/dev/api/)

### 项目文档
- `README.md` - 项目说明
- `IMPLEMENTATION.md` - 详细实现文档
- `PAGE_TEMPLATE.md` - 页面开发模板
- `CHECKLIST.md` - 功能检查清单
- `SUMMARY.md` - 项目总结报告

### 开发建议
1. 先完成核心购物流程(分类、详情、结算、订单)
2. 参考已实现页面的代码风格
3. 使用封装好的工具函数和API
4. 保持代码风格统一
5. 做好异常处理

---

## 技术支持

### 项目位置
```
/Users/shengmingxing/fruit_sale/mini_app
```

### 关键文件
- **应用配置**: `app.json`
- **应用入口**: `app.js`
- **请求封装**: `utils/request.js`
- **主题系统**: `utils/theme.js`

### 联系方式
如有问题请参考项目文档或查看微信小程序官方文档。

---

## 下一步

1. **阅读文档** - 查看 `README.md` 和 `IMPLEMENTATION.md`
2. **配置项目** - 设置AppID和后端地址
3. **准备资源** - 准备必需的图片资源
4. **开始开发** - 参考模板开发剩余页面
5. **测试功能** - 完成后进行全面测试

---

**提示**: 项目核心架构已完成,可以快速开发剩余页面。建议优先完成核心购物流程页面。

**祝开发顺利!** 🎉