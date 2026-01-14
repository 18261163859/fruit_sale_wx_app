# 高端云南水果销售系统 - 后台管理

## 项目简介

高端云南水果线上销售平台后台管理系统，基于 Spring Boot 3 + MyBatis Plus + MySQL + Redis 构建。

## 技术栈

- **核心框架**: Spring Boot 3.2.0
- **持久层**: MyBatis Plus 3.5.5
- **数据库**: MySQL 8.4.6
- **缓存**: Redis 7.0
- **连接池**: Druid 1.2.20
- **安全认证**: JWT (JJWT 0.12.3)
- **API 文档**: Knife4j 4.4.0
- **工具类**: Hutool 5.8.23
- **JSON 处理**: FastJSON2 2.0.43

## 项目结构

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/fruit/sale/
│   │   │   ├── aspect/          # 切面
│   │   │   ├── common/          # 公共类（统一响应等）
│   │   │   ├── config/          # 配置类
│   │   │   ├── constant/        # 常量
│   │   │   ├── controller/      # 控制器
│   │   │   ├── dto/             # 数据传输对象
│   │   │   ├── entity/          # 实体类
│   │   │   ├── enums/           # 枚举
│   │   │   ├── exception/       # 异常处理
│   │   │   ├── interceptor/     # 拦截器
│   │   │   ├── mapper/          # MyBatis Mapper
│   │   │   ├── service/         # 业务逻辑层
│   │   │   ├── utils/           # 工具类
│   │   │   └── vo/              # 视图对象
│   │   └── resources/
│   │       ├── mapper/          # MyBatis XML
│   │       ├── application.yml  # 配置文件
│   │       ├── application-dev.yml
│   │       └── application-prod.yml
│   └── test/                    # 测试代码
├── pom.xml                      # Maven 配置
└── README.md                    # 项目说明
```

## 快速开始

### 环境要求

- JDK 17+
- Maven 3.6+
- MySQL 8.0+
- Redis 7.0+

### 启动步骤

1. **克隆项目**
   ```bash
   git clone <repository-url>
   cd backend
   ```

2. **启动数据库**
   
   确保 MySQL 和 Redis 已启动（可使用项目根目录的 docker-compose.yml）:
   ```bash
   cd ..
   docker-compose up -d
   ```

3. **导入数据库**
   
   执行项目根目录下的 `database_design.sql` 文件

4. **配置应用**
   
   修改 `src/main/resources/application-dev.yml` 中的数据库和 Redis 配置（如需要）

5. **启动应用**
   ```bash
   mvn spring-boot:run
   ```
   
   或者使用 IDE 直接运行 `FruitSaleApplication` 类

6. **访问应用**
   - API 文档: http://localhost:8000/api/doc.html
   - Druid 监控: http://localhost:8000/api/druid (admin/admin123)
   - 健康检查: http://localhost:8000/api/health/check

## 核心功能

### 已实现

- ✅ 统一响应封装 (Result)
- ✅ 全局异常处理
- ✅ MyBatis Plus 集成（分页、逻辑删除）
- ✅ Redis 集成
- ✅ JWT 认证
- ✅ 跨域配置
- ✅ API 文档（Knife4j）
- ✅ 请求日志记录
- ✅ Druid 数据源监控

### 待开发

- ⏳ 用户管理模块
- ⏳ 商品管理模块
- ⏳ 订单管理模块
- ⏳ 代理管理模块
- ⏳ 积分管理模块
- ⏳ 物流管理模块
- ⏳ 财务报表模块

## 开发规范

### 代码规范

- 遵循阿里巴巴 Java 开发手册
- 使用 Lombok 简化代码
- Controller 层只做参数校验和调用 Service
- Service 层处理业务逻辑
- Mapper 层只做数据访问

### 命名规范

- **类名**: 大驼峰（PascalCase）
- **方法名/变量名**: 小驼峰（camelCase）
- **常量**: 全大写下划线分隔（UPPER_SNAKE_CASE）
- **包名**: 全小写

### 数据库规范

- 表名、字段名: 小写下划线分隔
- 主键: `id` (BIGINT AUTO_INCREMENT)
- 创建时间: `create_time` (DATETIME)
- 更新时间: `update_time` (DATETIME)
- 逻辑删除: `is_deleted` (TINYINT 0/1)

## API 接口说明

### 认证相关

所有需要认证的接口需在请求头中携带 Token:

```
Authorization: Bearer <your-token>
```

### 统一响应格式

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1234567890
}
```

## 常见问题

### 1. 启动时提示数据库连接失败

检查 MySQL 是否启动，以及配置文件中的连接信息是否正确。

### 2. Redis 连接失败

检查 Redis 是否启动，默认端口为 6379。

### 3. API 文档无法访问

确保项目启动成功，访问地址为: http://localhost:8000/api/doc.html

## 许可证

Apache License 2.0

## 联系方式

- Email: support@fruit-sale.com
- 项目地址: <repository-url>
