# ADMIN WEB MODULE

**Stack:** Vue 3 + TypeScript + Vite + Element Plus + Pinia

## WHERE TO LOOK

| Task | Location |
|------|----------|
| API clients | `api/` (agent.ts, auth.ts, dashboard.ts, order.ts, product.ts, upload.ts, user.ts, other.ts) |
| State | `stores/` (app.ts, user.ts) |
| Views | `views/` (dashboard, user, product, order, agent, etc.) |
| Router | `router/index.ts` |
| Request | `utils/` (request.ts) |

## CONVENTIONS

- 2-space indentation
- PascalCase `.vue` components
- kebab-case directories
- `@` alias → `src/`
- Auto-imports: Element Plus + Vue APIs
- Axios wrappers per feature in `api/`

## ANTI-PATTERNS

- **No ESLint/Prettier** - linting unconfigured
- **No automated tests**
- **Manual QA** in PR process

## COMMANDS

```bash
npm install
npm run dev        # hot reload
npm run build      # production
npm run preview    # smoke test