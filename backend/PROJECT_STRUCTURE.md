# 项目结构说明

## 目录树

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/fruit/sale/
│   │   │   ├── FruitSaleApplication.java         # 主启动类
│   │   │   ├── aspect/                           # 切面包
│   │   │   │   └── LogAspect.java               # 日志切面（记录接口请求日志）
│   │   │   ├── common/                           # 公共类包
│   │   │   │   ├── PageResult.java              # 分页响应结果封装
│   │   │   │   ├── Result.java                  # 统一响应结果封装
│   │   │   │   └── ResultCode.java              # 响应状态码枚举
│   │   │   ├── config/                           # 配置类包
│   │   │   │   ├── CorsConfig.java              # 跨域配置
│   │   │   │   ├── Knife4jConfig.java           # API文档配置
│   │   │   │   ├── MybatisPlusConfig.java       # MyBatis Plus配置
│   │   │   │   ├── RedisConfig.java             # Redis配置
│   │   │   │   └── WebMvcConfig.java            # Web MVC配置
│   │   │   ├── constant/                         # 常量包（待扩展）
│   │   │   ├── controller/                       # 控制器包
│   │   │   │   └── HealthController.java        # 健康检查控制器
│   │   │   ├── dto/                              # 数据传输对象包（待扩展）
│   │   │   ├── entity/                           # 实体类包（待扩展）
│   │   │   ├── enums/                            # 枚举包（待扩展）
│   │   │   ├── exception/                        # 异常处理包
│   │   │   │   ├── BusinessException.java       # 业务异常类
│   │   │   │   └── GlobalExceptionHandler.java  # 全局异常处理器
│   │   │   ├── interceptor/                      # 拦截器包
│   │   │   │   └── AuthInterceptor.java         # JWT认证拦截器
│   │   │   ├── mapper/                           # MyBatis Mapper接口包（待扩展）
│   │   │   ├── service/                          # 业务逻辑层包（待扩展）
│   │   │   ├── utils/                            # 工具类包
│   │   │   │   ├── JwtUtils.java                # JWT工具类
│   │   │   │   └── RedisUtils.java              # Redis工具类
│   │   │   └── vo/                               # 视图对象包（待扩展）
│   │   └── resources/
│   │       ├── mapper/                           # MyBatis XML映射文件目录
│   │       ├── static/                           # 静态资源目录
│   │       ├── templates/                        # 模板文件目录
│   │       ├── application.yml                   # 主配置文件
│   │       ├── application-dev.yml               # 开发环境配置
│   │       └── application-prod.yml              # 生产环境配置
│   └── test/                                     # 测试代码目录
│       └── java/com/fruit/sale/
├── .gitignore                                    # Git忽略配置
├── pom.xml                                       # Maven项目配置
├── README.md                                     # 项目说明文档
├── PROJECT_STRUCTURE.md                          # 项目结构说明（本文件）
└── start.sh                                      # 启动脚本
```

## 核心组件说明

### 1. 配置层 (config/)

- **CorsConfig**: 处理跨域请求，允许前端调用
- **Knife4jConfig**: 配置 Swagger API 文档
- **MybatisPlusConfig**: 配置分页、乐观锁、防全表更新等插件
- **RedisConfig**: 配置 Redis 序列化方式
- **WebMvcConfig**: 配置拦截器和静态资源映射

### 2. 公共类 (common/)

- **Result\<T\>**: 统一响应格式，包含 code、message、data、timestamp
- **PageResult\<T\>**: 分页响应格式，包含 records、total、current、size、pages
- **ResultCode**: 定义所有响应状态码（成功、失败、业务异常等）

### 3. 异常处理 (exception/)

- **BusinessException**: 业务异常类，可携带自定义错误码和消息
- **GlobalExceptionHandler**: 全局异常处理器，统一处理各类异常并返回友好提示

### 4. 拦截器 (interceptor/)

- **AuthInterceptor**: JWT 认证拦截器
  - 验证请求头中的 Token
  - 检查 Token 是否有效和过期
  - 将用户ID存入请求属性

### 5. 切面 (aspect/)

- **LogAspect**: 日志切面
  - 拦截所有 Controller 方法
  - 记录请求地址、方法、参数
  - 记录响应结果和执行时间

### 6. 工具类 (utils/)

- **JwtUtils**: JWT 工具类
  - 生成 Token
  - 解析 Token
  - 验证 Token 有效性
  - 获取用户信息

- **RedisUtils**: Redis 工具类
  - 基础的增删改查操作
  - 过期时间设置
  - 自增自减操作

### 7. 控制器 (controller/)

- **HealthController**: 健康检查控制器
  - `/health/check`: 检查系统运行状态
  - `/health/info`: 获取系统基本信息

## 配置文件说明

### application.yml (主配置)

- 服务器端口: 8000
- 上下文路径: /api
- 数据库连接池配置
- Redis 连接配置
- MyBatis Plus 配置
- JWT 配置
- 日志配置

### application-dev.yml (开发环境)

- 本地数据库连接
- 本地 Redis 连接
- Debug 级别日志

### application-prod.yml (生产环境)

- 生产数据库连接（支持环境变量）
- 生产 Redis 连接（支持环境变量）
- Info 级别日志
- 日志文件路径

## 依赖说明

### 核心依赖

| 依赖 | 版本 | 说明 |
|-----|------|-----|
| Spring Boot | 3.2.0 | 核心框架 |
| MyBatis Plus | 3.5.5 | ORM 框架 |
| Druid | 1.2.20 | 数据库连接池 |
| MySQL Connector | 最新 | MySQL 驱动 |
| Redis | 最新 | Redis 客户端 |
| JJWT | 0.12.3 | JWT 实现 |
| Hutool | 5.8.23 | Java 工具集 |
| FastJSON2 | 2.0.43 | JSON 处理 |
| Knife4j | 4.4.0 | API 文档 |
| Lombok | 1.18.30 | 代码简化 |

## 接口规范

### 请求规范

1. **认证接口**: 不需要 Token，路径以 `/auth/` 开头
2. **业务接口**: 需要 Token，在请求头中携带:
   ```
   Authorization: Bearer <token>
   ```

### 响应规范

所有接口统一返回 `Result<T>` 格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {},
  "timestamp": 1696089600000
}
```

分页接口返回 `Result<PageResult<T>>` 格式:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "records": [],
    "total": 100,
    "current": 1,
    "size": 10,
    "pages": 10
  },
  "timestamp": 1696089600000
}
```

## 开发指南

### 新增业务模块步骤

1. **创建实体类** (entity/)
   - 继承或使用 MyBatis Plus 注解
   - 添加必要的字段和注解

2. **创建 Mapper 接口** (mapper/)
   - 继承 `BaseMapper<T>`
   - 添加自定义 SQL 方法（如需要）

3. **创建 Mapper XML** (resources/mapper/)
   - 编写复杂 SQL 查询

4. **创建 Service 接口和实现类** (service/)
   - 定义业务方法
   - 实现业务逻辑

5. **创建 DTO 和 VO** (dto/, vo/)
   - DTO: 接收前端参数
   - VO: 返回给前端的数据

6. **创建 Controller** (controller/)
   - 添加 `@RestController` 和 `@RequestMapping`
   - 使用 Swagger 注解
   - 调用 Service 方法
   - 返回 `Result<T>`

### 开发建议

1. **分层清晰**: Controller -> Service -> Mapper
2. **异常处理**: 使用 `BusinessException` 抛出业务异常
3. **参数校验**: 使用 JSR-303 注解进行参数校验
4. **日志记录**: 关键业务使用 `log.info/warn/error`
5. **事务管理**: Service 层方法添加 `@Transactional`
6. **代码复用**: 抽取公共方法到工具类

## 下一步开发计划

基于 `database_design.sql` 中的表结构，需要开发以下模块：

1. ✅ 用户管理（用户信息、地址、认证）
2. ✅ 商品管理（分类、商品信息）
3. ✅ 订单管理（订单、订单项、物流）
4. ✅ 代理管理（代理申请、返现记录）
5. ✅ 积分管理（积分记录、积分卡）
6. ✅ 系统配置（轮播图、系统参数）
7. ✅ 分享管理（分享记录）

每个模块需包含：
- Entity（对应数据库表）
- Mapper（数据访问）
- Service（业务逻辑）
- Controller（接口层）
- DTO/VO（数据传输）

## 测试访问

启动项目后，可访问：

- **API 文档**: http://localhost:8000/api/doc.html
- **Druid 监控**: http://localhost:8000/api/druid
- **健康检查**: http://localhost:8000/api/health/check
- **系统信息**: http://localhost:8000/api/health/info
