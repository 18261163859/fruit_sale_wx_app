# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

高端云南水果销售平台 (Yunnan Fruit Sales Platform) - A three-tier e-commerce system:
- **backend/**: Spring Boot 3.2.0 REST API (Java 17)
- **admin_web/**: Vue 3 + TypeScript admin dashboard
- **mini_app/**: WeChat Mini Program for users

## Build & Run Commands

### Backend (Port 8000)
```bash
cd backend
mvn spring-boot:run                    # Start development server
mvn clean package -DskipTests          # Production build
mvn test                               # Run tests
```
API Documentation: http://localhost:8000/api/doc.html
Druid Monitor: http://localhost:8000/api/druid (admin/admin123)

### Admin Web (Port 5173)
```bash
cd admin_web
npm install                            # Install dependencies
npm run dev                            # Development server
npm run build                          # Production build
```

### Mini App
Open `/mini_app` in WeChat Developer Tools. Update backend URL in `utils/request.js`.

## Architecture

```
┌─────────────────┐     ┌─────────────────┐     ┌─────────────────┐
│  Mini App       │     │  Admin Web      │     │  Backend        │
│  (WeChat)       │────▶│  (Vue 3)        │────▶│  (Spring Boot)  │
│  Port: N/A      │     │  Port: 5173     │     │  Port: 8000     │
└─────────────────┘     └─────────────────┘     └─────────────────┘
                                                        │
                                                        ▼
                                                ┌─────────────────┐
                                                │  MySQL 8.4.6    │
                                                │  Redis 7.0      │
                                                │  MinIO          │
                                                └─────────────────┘
```

All HTTP requests use Bearer Token authentication. API base path: `/api`.

## Database Schema (16 tables)

- **Users**: admin_user, user_info, user_address, agent_apply
- **Products**: product_category, product_info
- **Orders**: order_info, order_item, order_logistics
- **Finance**: commission_record, integral_record, integral_card
- **Config**: banner_config, system_config, share_record, shopping_cart

User types: 0=normal, 1=VIP, 2=level-1 agent, 3=level-2 agent

## API Response Format

```json
{
  "code": 200,
  "message": "success",
  "data": {},
  "timestamp": 1234567890
}
```

## Key Configuration

- **Database**: localhost:3306/fruit_sale
- **Redis**: localhost:6379
- **MinIO**: http://192.168.0.133:9000
- **JWT Expiration**: 2 hours
- **Max Upload**: 100MB

## Backend Code Structure

```
backend/src/main/java/com/fruit/sale/
├── controller/    # REST endpoints
├── service/       # Business logic
├── mapper/        # MyBatis Plus database mappers
├── entity/        # Database entities
├── dto/           # Request/Response DTOs
├── vo/            # View objects
├── config/        # Spring configuration
├── interceptor/   # Auth interceptors
└── exception/     # Custom exceptions
```

## Code Conventions

- **Backend**: 4-space indent, Lombok annotations, camelCase
- **Frontend**: 2-space indent, PascalCase components, kebab-case directories
- **Commits**: Conventional format (`feat:`, `fix:`, `refactor:`)

## Development Notes

- Admin web proxies `/api/*` to `http://localhost:8080/*` in dev mode
- All tables use soft delete (`is_deleted` field)
- Agent commission is calculated automatically on order completion
- Points (integral) system supports both earning and spending
