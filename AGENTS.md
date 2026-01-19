# PROJECT KNOWLEDGE BASE

**Generated:** 2026-01-14
**Branch:** main

## OVERVIEW

Fruit sales platform with 3 clients: Spring Boot REST API (backend), Vue 3 admin console (admin_web), WeChat Mini Program (mini_app). Database schema and seeds at root level.

## STRUCTURE

```
fruit_sale/
├── backend/                    # Spring Boot 3.2.0 REST API (port 8000)
│   ├── src/main/java/com/fruit/sale/
│   │   ├── controller/         # REST endpoints (plural paths)
│   │   ├── service/           # Business logic
│   │   ├── entity/            # JPA entities
│   │   ├── mapper/            # MyBatis Plus mappers
│   │   ├── dto/               # Request objects
│   │   ├── vo/                # Response objects
│   │   ├── config/            # Spring configs (Redis, JWT, MinIO)
│   │   ├── common/            # Shared utilities
│   │   ├── aspect/            # AOP (logging, transaction)
│   │   ├── constant/           # Constants
│   │   ├── enums/             # Enumerations
│   │   ├── exception/         # Custom exceptions
│   │   ├── interceptor/        # Request interceptors
│   │   └── utils/             # Utility classes
│   └── src/main/resources/    # application.yml, application-dev.yml
├── admin_web/                  # Vue 3 + TypeScript + Vite SPA
│   └── src/
│       ├── api/               # Axios API clients (8 modules)
│       ├── stores/            # Pinia stores (app, user)
│       ├── views/             # Routed views (10 modules)
│       ├── components/        # Shared components
│       ├── layouts/           # Page layouts
│       ├── router/            # Vue Router config
│       ├── utils/             # Front utilities
│       └── styles/            # Global styles
├── mini_app/                   # WeChat Mini Program
│   ├── api/                   # Backend API wrappers (9 modules)
│   ├── utils/                 # Utilities (request, theme, image)
│   ├── pages/                 # 18 feature pages
│   ├── components/            # Mini program components
│   └── styles/                # Page styles
├── database_design.sql         # Schema (execute first)
├── init_data.sql              # Seed data
└── fix_*.sql                  # Migration scripts
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
| File upload | `backend/controller/` + `mini_app/utils/upload.js` | MinIO backend |

## CONVENTIONS (DEVIATIONS FROM STANDARD)

**Backend (Java):**
- 4-space indentation
- PascalCase classes, camelCase members
- Lombok for boilerplate reduction
- Plural REST paths (`/orders` not `/order`)
- Service interfaces with `I` prefix discouraged

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

## ANTI-PATTERNS (THIS PROJECT)

- **No ESLint/Prettier configs** - linting/formatting unconfigured
- **Minimal test coverage** - only utility file in `src/test/java/`, no actual JUnit tests
- **Frontend no automation** - manual QA required
- **Duplicate files** - `import-images.sh` exists twice
- **Empty placeholder files** - `backend/echo`, `backend/PUT`

## BUILD & CI

| Component | Dev | Production |
|-----------|-----|------------|
| Backend | `mvn spring-boot:run` | `./backend/start.sh` or Docker |
| Admin Web | `npm run dev` | `npm run build` → Docker nginx |
| Mini App | WeChat DevTools | WeChat CI pipeline |
| All images | - | `./build-and-export.sh` |

**Docker images:**
- Backend: Multi-stage, Maven mirror, Chinese fonts, JVM opts
- Admin Web: Node 20 builder, nginx:alpine runtime
- Export: `fruit-sale-images-*.tar.gz` with import script

## TESTING

- **Backend**: JUnit 5 + Mockito configured but minimally used
- **Admin Web**: No automated tests
- **Mini App**: No automated tests

## KEY CONFIGURATIONS

| File | Purpose |
|------|---------|
| `backend/src/main/resources/application-dev.yml` | Local DB, Redis, WeChat creds |
| `backend/pom.xml` | Spring Boot 3.2.0, MyBatis Plus, JWT, MinIO |
| `admin_web/vite.config.ts` | Vite + auto-import plugins |
| `mini_app/project.config.json` | WeChat appid, compile settings |
| `docker-compose.yaml` | Local dev environment |

## NOTES

- Secret values in `application-dev.yml` - never commit production creds
- Database scripts at root level - keep synchronized across modules
- Mini App theme system: blue/white (regular), black/gold (VIP), red/gold (New Year)
- API docs: `/api/doc.html` (Knife4j) | Druid monitor: `/api/druid`