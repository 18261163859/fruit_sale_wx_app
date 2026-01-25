# PROJECT KNOWLEDGE BASE

**Generated:** 2026-01-22
**Branch:** main

## OVERVIEW

Fruit sales platform with 3 clients: Spring Boot REST API (backend, port 8000), Vue 3 admin console (admin_web, port 5173), WeChat Mini Program (mini_app). Database schema and seeds at root level.

## STRUCTURE

```
fruit_sale/
├── backend/                    # Spring Boot 3.2.0 REST API (port 8000)
│   ├── src/main/java/com/fruit/sale/
│   │   ├── controller/         # REST endpoints (plural paths, 19 files)
│   │   ├── service/            # Business logic (19 files)
│   │   ├── entity/             # JPA entities (24 files)
│   │   ├── mapper/             # MyBatis Plus mappers (23 files)
│   │   ├── dto/                # Request objects (32 files)
│   │   ├── vo/                 # Response objects (24 files)
│   │   ├── config/             # Spring configs (10 files)
│   │   ├── common/             # Shared utilities
│   │   ├── aspect/             # AOP (logging)
│   │   ├── constant/           # Constants
│   │   ├── enums/              # Enumerations
│   │   ├── exception/          # Custom exceptions
│   │   ├── interceptor/        # Request interceptors
│   │   └── utils/              # Utility classes
│   └── src/main/resources/     # application.yml, application-dev.yml
├── admin_web/                  # Vue 3 + TypeScript + Vite SPA
│   └── src/
│       ├── api/                # Axios API clients (8 modules)
│       ├── stores/             # Pinia stores (app, user)
│       ├── views/              # Routed views (10 modules)
│       ├── components/         # Shared components
│       ├── layouts/            # Page layouts
│       ├── router/             # Vue Router config
│       ├── utils/              # Front utilities
│       └── styles/             # Global styles
├── mini_app/                   # WeChat Mini Program
│   ├── api/                   # Backend API wrappers (9 modules)
│   ├── utils/                 # Utilities (request, theme, image)
│   ├── pages/                 # 18 feature pages
│   ├── components/            # Mini program components
│   └── styles/                # Page styles
├── database_design.sql         # Schema (execute first)
├── init_data.sql               # Seed data
├── fix_*.sql                   # Migration scripts
└── docker-compose.yaml         # Local dev (MySQL, Redis, MinIO)
```

## WHERE TO LOOK

| Task | Location | Notes |
|------|----------|-------|
| REST endpoints | `backend/controller/` | Plural paths (`/orders`) |
| Business logic | `backend/service/` | Transaction boundaries |
| Database design | `database_design.sql` | Run first, then init_data.sql |
| Admin API calls | `admin_web/src/api/` | Axios-based |
| Mini App APIs | `mini_app/api/` | camelCase functions |
| Auth token logic | `backend/config/` + `admin_web/src/stores/` | JWT + Pinia |
| File upload | `backend/controller/UploadController.java` + `mini_app/utils/upload.js` | MinIO backend |
| WeChat integration | `backend/config/WeChatConfig.java` | SDK integration |
| API documentation | `/api/doc.html` (Knife4j) | Druid: `/api/druid` |

## ENTRY POINTS

| Module | File | Purpose |
|--------|------|---------|
| Backend | `backend/src/main/java/com/fruit/sale/FruitSaleApplication.java` | Spring Boot main |
| Admin Web | `admin_web/src/main.ts` | Vue 3 app mount |
| Mini App | `mini_app/app.js` | WeChat lifecycle, theme init |
| Database | `database_design.sql` → `init_data.sql` | Schema → seeds |

## CONVENTIONS (DEVIATIONS FROM STANDARD)

**Backend (Java):**
- 4-space indentation
- PascalCase classes, camelCase members, UPPER_SNAKE_CASE constants
- Lombok for boilerplate reduction
- Plural REST paths (`/orders` not `/order`)
- No `I` prefix for service interfaces (interface in same file as impl)
- VO suffix for responses, DTO for requests

**Admin Web (Vue):**
- 2-space indentation
- PascalCase components (`.vue` files)
- kebab-case directories
- Path alias: `@` → `src/`
- Auto-import: Element Plus + Vue APIs

**Mini App:**
- camelCase functions
- Route-aligned folder naming (`pages/order-detail/`)
- ES6 enabled, minification active
- WeChat trial lib version

## ANTI-PATTERNS (THIS PROJECT)

**Critical:**
- **Security bypass**: `AdminUserServiceImpl.java` (lines 53-54) has temporary admin123 password bypass in production code
- **Hardcoded credentials**: IPs (127.0.0.1, 192.168.0.133, 121.43.104.109), passwords in multiple config files
- **Incomplete implementation**: WeChat phone decryption returns mock data (`AuthController.java` line 53)

**High:**
- **Points ratio hardcoded**: `OrderServiceImpl.java` (line 376) uses 1:1 instead of configurable
- **No ESLint/Prettier configs** - linting/formatting unconfigured
- **No automated tests** - JUnit/Mockito configured but unused
- **Empty placeholders**: `backend/echo`, `backend/PUT` files
- **Duplicate files**: `import-images.sh` exists in root and docker-images/

**Medium:**
- **FieldStrategy.ALWAYS**: Forces phone field insertion even when null (`UserInfo.java`)
- **Debug controller in production**: `TestController.java` for BCrypt testing

## BUILD & CI

| Component | Dev | Production |
|-----------|-----|------------|
| Backend | `mvn spring-boot:run` | `./backend/start.sh` or Docker |
| Admin Web | `npm run dev` (5173) | `npm run build` → Docker nginx |
| Mini App | WeChat DevTools | WeChat CI pipeline |
| All images | - | `./build-and-export.sh` |

**Docker:**
- Local: `docker-compose.yaml` (5 services + fruit-network)
- Production: `docker-images/docker-compose.yaml` with Nginx proxy + SSL
- Multi-stage builds with JVM optimization, Chinese fonts

**Testing:** Manual only across all modules

## KEY CONFIGURATIONS

| File | Purpose |
|------|---------|
| `backend/src/main/resources/application-dev.yml` | Local DB, Redis, WeChat creds |
| `backend/pom.xml` | Spring Boot 3.2.0, MyBatis Plus, JWT, MinIO, WeChat SDK |
| `admin_web/vite.config.ts` | Vite + auto-import plugins, path alias |
| `mini_app/project.config.json` | WeChat appid, compile settings |
| `mini_app/utils/request.js` | Base URL, token auth |
| `docker-compose.yaml` | Local dev environment |

## NOTES

- Secret values in `application-dev.yml` - never commit production creds
- Database scripts at root level - keep synchronized across modules
- Mini App theme system: blue/white (regular), black/gold (VIP), red/gold (New Year)
- JWT expiration: 2 hours
- Max upload: 100MB
- Soft delete field: `is_deleted` across all tables

## DEVELOPMENT WORKFLOW

```bash
# Start local environment
docker-compose up -d

# Start backend (port 8000)
cd backend && mvn spring-boot:run

# Start admin web (port 5173)
cd admin_web && npm run dev

# Mini App: open in WeChat DevTools
# Update mini_app/utils/request.js BASE_URL
```