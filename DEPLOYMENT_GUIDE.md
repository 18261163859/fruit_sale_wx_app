# 高端云南水果销售平台 - 部署启动文档

## 📋 目录

- [系统概述](#系统概述)
- [环境要求](#环境要求)
- [数据库配置](#数据库配置)
- [后端启动](#后端启动)
- [后台管理系统启动](#后台管理系统启动)
- [微信小程序配置](#微信小程序配置)
- [常见问题](#常见问题)
- [接口文档](#接口文档)

---

## 系统概述

本系统包含三个子项目：

1. **backend** - SpringBoot后端API服务
2. **admin_web** - Vue3后台管理系统
3. **mini_app** - 微信小程序用户端

---

## 环境要求

### 必需软件

| 软件 | 版本要求 | 用途 |
|------|---------|------|
| JDK | 17+ | 后端运行环境 |
| Maven | 3.6+ | 后端依赖管理 |
| MySQL | 8.0+ | 数据库 |
| Node.js | 18+ | 前端运行环境 |
| npm | 9+ | 前端包管理器 |
| 微信开发者工具 | 最新版 | 小程序开发 |

### 检查环境

```bash
# 检查Java版本
java -version

# 检查Maven版本
mvn -v

# 检查Node.js版本
node -v

# 检查npm版本
npm -v

# 检查MySQL版本
mysql --version
```

---

## 数据库配置

### 1. 创建数据库

```bash
# 登录MySQL
mysql -u root -p

# 创建数据库
CREATE DATABASE fruit_sale DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

# 退出
exit;
```

### 2. 导入数据库表结构

```bash
# 进入项目根目录
cd /Users/shengmingxing/fruit_sale

# 导入SQL文件
mysql -u root -p fruit_sale < database_design.sql
```

### 3. 验证表是否创建成功

```sql
USE fruit_sale;
SHOW TABLES;

-- 应该看到以下表：
-- admin_user
-- agent_apply
-- banner_config
-- commission_record
-- integral_card
-- integral_record
-- order_info
-- order_item
-- order_logistics
-- product_category
-- product_info
-- product_review
-- share_record
-- shopping_cart
-- system_config
-- user_address
-- user_info
```

### 4. 初始化管理员账号

```sql
USE fruit_sale;

-- 插入默认管理员账号 (用户名: admin, 密码: admin123)
INSERT INTO `admin_user` (`username`, `password`, `real_name`, `role_type`, `status`)
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lIzYoAYFJgA8HGF/a', '系统管理员', 1, 1);
```

---

## 后端启动

### 1. 配置数据库连接

编辑后端配置文件：

```bash
# 打开配置文件
cd /Users/shengmingxing/fruit_sale/backend
vi src/main/resources/application.yml
```

修改数据库配置：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/fruit_sale?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root          # 修改为你的数据库用户名
    password: your_password  # 修改为你的数据库密码
```

### 2. 配置Redis（可选）

如果使用Redis缓存，需要配置：

```yaml
spring:
  data:
    redis:
      host: localhost
      port: 6379
      password:  # 如果有密码则填写
      database: 0
```

### 3. 启动后端服务

**方式一：使用Maven命令**

```bash
cd /Users/shengmingxing/fruit_sale/backend

# 清理并打包
mvn clean package -DskipTests

# 启动服务
mvn spring-boot:run
```

**方式二：使用IDE启动**

1. 用IDEA打开 `/Users/shengmingxing/fruit_sale/backend` 项目
2. 找到 `com.fruit.sale.FruitSaleApplication` 主类
3. 右键点击 -> Run 'FruitSaleApplication'

### 4. 验证启动成功

启动成功后，会看到类似输出：

```
  ____             _   _     ____        _
 |  _ \           (_) | |   / ___|  __ _| | ___
 | |_) |  _ __ ___ _| | |   \___ \ / _` | |/ _ \
 |  __/  | '__| | | | | |    ___) | (_| | |  __/
 |_|     |_|  |_| |_|_|_|   |____/ \__,_|_|\___|

Application 'fruit-sale' is running!
Access URLs:
----------------------------------------------------------
    Local:      http://localhost:8080
    Swagger:    http://localhost:8080/doc.html
----------------------------------------------------------
```

访问测试：

```bash
# 健康检查
curl http://localhost:8080/health

# 访问API文档
open http://localhost:8080/doc.html
```

---

## 后台管理系统启动

### 1. 安装依赖

```bash
cd /Users/shengmingxing/fruit_sale/admin_web

# 安装依赖（首次运行需要）
npm install
```

### 2. 配置后端API地址

如果后端地址不是 `http://localhost:8080`，需要修改配置：

```bash
# 编辑配置文件
vi vite.config.ts
```

修改proxy配置：

```typescript
server: {
  port: 5173,
  proxy: {
    '/api': {
      target: 'http://localhost:8080',  // 修改为你的后端地址
      changeOrigin: true,
      rewrite: (path) => path.replace(/^\/api/, '')
    }
  }
}
```

### 3. 启动开发服务器

```bash
cd /Users/shengmingxing/fruit_sale/admin_web

# 开发模式启动
npm run dev
```

启动成功后输出：

```
  VITE v5.x.x  ready in xxx ms

  ➜  Local:   http://localhost:5173/
  ➜  Network: http://192.168.x.x:5173/
  ➜  press h + enter to show help
```

### 4. 访问后台管理系统

浏览器打开：http://localhost:5173

**默认登录账号**：
- 用户名：`admin`
- 密码：`admin123`

### 5. 生产环境构建

```bash
cd /Users/shengmingxing/fruit_sale/admin_web

# 构建生产版本
npm run build

# 构建完成后，dist目录包含可部署文件
# 将dist目录部署到Nginx等Web服务器
```

---

## 微信小程序配置

### 1. 导入项目

1. 打开**微信开发者工具**
2. 点击 "+" 导入项目
3. 项目路径选择：`/Users/shengmingxing/fruit_sale/mini_app`
4. AppID：填写你的小程序AppID（测试可选择"测试号"）
5. 项目名称：高端云南水果

### 2. 配置后端API地址

编辑配置文件：

```bash
vi /Users/shengmingxing/fruit_sale/mini_app/utils/config.js
```

修改API地址：

```javascript
module.exports = {
  // 开发环境
  apiBaseUrl: 'http://localhost:8080',  // 修改为你的后端地址

  // 生产环境
  // apiBaseUrl: 'https://api.yourdomain.com',

  // 其他配置...
}
```

### 3. 配置小程序信息

编辑 `app.json`：

```json
{
  "window": {
    "navigationBarTitleText": "高端云南水果",
    "navigationBarTextStyle": "black",
    "navigationBarBackgroundColor": "#ffffff"
  }
}
```

### 4. 配置服务器域名（正式发布需要）

在微信公众平台（mp.weixin.qq.com）配置服务器域名：

1. 登录微信公众平台
2. 开发 -> 开发管理 -> 开发设置
3. 服务器域名配置：
   - request合法域名：`https://your-api-domain.com`
   - uploadFile合法域名：`https://your-api-domain.com`
   - downloadFile合法域名：`https://your-api-domain.com`

### 5. 本地调试配置

在微信开发者工具中：

1. 点击右上角"详情"
2. 勾选"不校验合法域名、web-view（业务域名）、TLS 版本以及 HTTPS 证书"
3. 重启项目

### 6. 启动小程序

在微信开发者工具中点击"编译"按钮即可预览小程序。

---

## 常见问题

### 1. 后端启动失败

**问题：数据库连接失败**

```
Error: Could not connect to database
```

解决方案：
- 检查MySQL是否启动：`mysql.server status`
- 检查数据库配置是否正确
- 检查数据库用户权限

**问题：端口被占用**

```
Port 8080 is already in use
```

解决方案：
```bash
# 查找占用端口的进程
lsof -i :8080

# 杀死进程
kill -9 <PID>

# 或修改application.yml中的端口
server:
  port: 8081
```

### 2. 前端启动失败

**问题：依赖安装失败**

```
npm ERR! code ERESOLVE
```

解决方案：
```bash
# 清理缓存
npm cache clean --force

# 删除node_modules和package-lock.json
rm -rf node_modules package-lock.json

# 重新安装
npm install
```

**问题：API请求跨域**

解决方案：
- 确保vite.config.ts中配置了proxy
- 或在后端添加CORS配置（已配置在CorsConfig.java）

### 3. 小程序常见问题

**问题：接口调用失败**

解决方案：
- 检查是否勾选"不校验合法域名"
- 检查后端服务是否启动
- 检查config.js中的API地址是否正确

**问题：登录失败**

解决方案：
- 确保使用真实AppID（测试号可能无法获取手机号）
- 检查后端wx.login接口是否正常

### 4. 数据库问题

**问题：表不存在**

```
Table 'fruit_sale.user_info' doesn't exist
```

解决方案：
```bash
# 重新导入数据库
mysql -u root -p fruit_sale < database_design.sql
```

---

## 接口文档

### Swagger API文档

启动后端后，访问：

```
http://localhost:8080/doc.html
```

可以查看和测试所有API接口。

### 主要接口模块

| 模块 | 说明 | 接口数量 |
|------|------|---------|
| 认证管理 | 登录注册 | 2 |
| 用户管理 | 用户信息、会员、积分 | 3 |
| 商品管理 | 商品查询、分类 | 5 |
| 订单管理 | 订单CRUD、支付、发货 | 8 |
| 购物车管理 | 购物车操作 | 6 |
| 地址管理 | 收货地址 | 6 |
| 代理管理 | 代理申请、团队、返现 | 4 |
| 分享管理 | 分享链接、记录 | 2 |
| 轮播图 | 首页轮播 | 1 |
| 后台管理 | 管理员功能 | 14 |

---

## 快速启动脚本

创建一键启动脚本（可选）：

### start_backend.sh

```bash
#!/bin/bash
cd /Users/shengmingxing/fruit_sale/backend
echo "🚀 Starting Backend Service..."
mvn spring-boot:run
```

### start_admin.sh

```bash
#!/bin/bash
cd /Users/shengmingxing/fruit_sale/admin_web
echo "🚀 Starting Admin Web..."
npm run dev
```

### start_all.sh

```bash
#!/bin/bash

# 启动后端
echo "🚀 Starting Backend..."
cd /Users/shengmingxing/fruit_sale/backend
mvn spring-boot:run &

# 等待后端启动
sleep 10

# 启动前端
echo "🚀 Starting Admin Web..."
cd /Users/shengmingxing/fruit_sale/admin_web
npm run dev &

echo "✅ All services started!"
echo "Backend: http://localhost:8080"
echo "Admin: http://localhost:5173"
```

使用方式：

```bash
# 添加执行权限
chmod +x start_all.sh

# 运行
./start_all.sh
```

---

## 系统架构图

```
┌─────────────────┐
│  微信小程序端    │
│  (mini_app)     │
└────────┬────────┘
         │
         │ HTTP/HTTPS
         │
┌────────▼────────┐     ┌──────────────┐
│  后端API服务    │────▶│   MySQL      │
│  (backend)      │     │  数据库      │
│  Port: 8080     │     └──────────────┘
└────────┬────────┘
         │
         │ HTTP
         │
┌────────▼────────┐
│ 后台管理系统    │
│ (admin_web)     │
│ Port: 5173      │
└─────────────────┘
```

---

## 生产环境部署建议

### 1. 后端部署

- 使用 `java -jar` 或 Docker 容器化部署
- 配置Nginx反向代理
- 启用HTTPS
- 配置日志输出
- 添加监控和告警

### 2. 前端部署

- 构建生产版本：`npm run build`
- 部署到Nginx/CDN
- 启用Gzip压缩
- 配置缓存策略

### 3. 数据库

- 定期备份
- 主从复制
- 性能优化（索引、查询优化）
- 监控慢查询

### 4. 小程序发布

- 提交代码审核
- 配置合法域名
- 启用HTTPS
- 配置支付功能

---

## 技术支持

如有问题，请查看：

- **项目文档**：`/Users/shengmingxing/fruit_sale/`
- **后端文档**：`backend/README.md`
- **前端文档**：`admin_web/README.md`
- **小程序文档**：`mini_app/README.md`

---

## 版本信息

- **项目版本**：v1.0.0
- **最后更新**：2024-09-30
- **作者**：Fruit Sale Team

---

**祝您部署顺利！🎉**