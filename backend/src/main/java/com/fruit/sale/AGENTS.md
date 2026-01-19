# BACKEND MODULE

**Package:** `com.fruit.sale`

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
| File upload | `controller/UploadController.java`, config/MinioConfig |
| WeChat integration | `config/WeChatConfig.java`, service/WeChatService |
| Logging | `aspect/LogAspect.java` |

## CONVENTIONS

- 4-space indentation, PascalCase, camelCase
- Lombok for constructors/getters/setters
- `I` prefix NOT used for service interfaces
- Service interfaces in same file as implementation (no separate interface files)
- VO suffix for response objects, DTO for requests

## ANTI-PATTERNS

- **Empty placeholders**: `echo`, `PUT` files at backend root
- **No interface segregation**: service interfaces merged with impl
- **Minimal testing**: only `GenerateToken.java` utility exists