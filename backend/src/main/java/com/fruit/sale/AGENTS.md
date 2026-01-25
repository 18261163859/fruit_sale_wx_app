# BACKEND MODULE

**Package:** `com.fruit.sale`
**Framework:** Spring Boot 3.2.0, Java 17

## STRUCTURE

```
com/fruit/sale/
├── controller/    # REST endpoints (19 files)
├── service/       # Business logic (19 files)
├── entity/        # JPA entities (24 files)
├── mapper/        # MyBatis mappers (23 files)
├── dto/           # Request DTOs (32 files)
├── vo/            # Response VOs (24 files)
├── config/        # Spring configs (10 files)
├── common/        # Utilities
├── aspect/        # AOP
├── constant/      # Constants
├── enums/         # Enums
├── exception/     # Exceptions
├── interceptor/   # Interceptors
└── utils/         # Tools
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| Auth logic | `config/JwtTokenUtils.java`, `interceptor/` |
| File upload | `controller/UploadController.java`, `config/MinioConfig` |
| WeChat integration | `config/WeChatConfig.java`, `service/WeChatService` |
| Logging | `aspect/LogAspect.java` |
| Exception handling | `exception/GlobalExceptionHandler.java` |

## ENTRY POINT

- `FruitSaleApplication.java` - Spring Boot main class

## CONVENTIONS

- 4-space indentation, PascalCase, camelCase
- Lombok for constructors/getters/setters
- `I` prefix NOT used for service interfaces
- Service interfaces in same file as implementation (no separate interface files)
- VO suffix for response objects, DTO for requests
- Plural REST paths (`/orders`)

## ANTI-PATTERNS (THIS MODULE)

**Critical:**
- **Security bypass**: `AdminUserServiceImpl.java` (lines 53-54) has temporary admin123 password bypass
- **Hardcoded credentials**: IPs and passwords in `application-dev.yml`

**High:**
- **Points ratio hardcoded**: `OrderServiceImpl.java` (line 376) uses 1:1 instead of configurable
- **Incomplete WeChat phone decryption**: `AuthController.java` (line 53) returns mock data
- **Empty placeholders**: `backend/echo`, `backend/PUT` files at module root

**Medium:**
- **FieldStrategy.ALWAYS**: Forces phone field insertion even when null (`UserInfo.java`)
- **Debug controller**: `TestController.java` for BCrypt testing in production

## BUILD & COMMANDS

```bash
mvn spring-boot:run              # Dev (port 8000)
mvn clean package -DskipTests    # Production build
mvn test                         # Run tests (configured but unused)
```

**API Documentation:** http://localhost:8000/api/doc.html (Knife4j)
**Druid Monitor:** http://localhost:8000/api/druid (admin/admin123)

## CONFIGURATION

| File | Purpose |
|------|---------|
| `application.yml` | Base config, MyBatis Plus, Knife4j, JWT |
| `application-dev.yml` | Local overrides, WeChat creds, MinIO |

## NOTES

- All tables use `is_deleted` for soft delete
- JWT expiration: 2 hours
- Max upload: 100MB
- Context path: `/api`