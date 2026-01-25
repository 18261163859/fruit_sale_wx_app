# ADMIN WEB MODULE

**Stack:** Vue 3 + TypeScript + Vite + Element Plus + Pinia
**Port:** 5173

## WHERE TO LOOK

| Task | Location |
|------|----------|
| API clients | `api/` (agent.ts, auth.ts, dashboard.ts, order.ts, product.ts, upload.ts, user.ts, other.ts) |
| State | `stores/` (app.ts, user.ts) |
| Views | `views/` (dashboard, user, product, order, agent, etc.) |
| Router | `router/index.ts` |
| Request | `utils/request.ts` |
| Config | `vite.config.ts` |

## ENTRY POINT

- `main.ts` - Vue 3 application mount

## CONVENTIONS

- 2-space indentation
- PascalCase `.vue` components
- kebab-case directories
- `@` alias → `src/`
- Auto-imports: Element Plus + Vue APIs
- Axios wrappers per feature in `api/`

## ANTI-PATTERNS (THIS MODULE)

**Critical:**
- **Hardcoded production IP**: `src/utils/index.ts` (line 152) has 121.43.104.109

**High:**
- **No ESLint/Prettier** - linting/formatting unconfigured
- **No automated tests** - manual QA required
- **No CI pipeline** configured

## COMMANDS

```bash
npm install
npm run dev        # hot reload (port 5173)
npm run build      # production
npm run preview    # smoke test
```

## PROXY CONFIG

Vite proxies `/api/*` → `http://localhost:8000/*` in dev mode

## KEY CONFIGURATIONS

| File | Purpose |
|------|---------|
| `vite.config.ts` | Vite + auto-import plugins, path alias |
| `tsconfig.json` | TypeScript project references |
| `package.json` | Dependencies, scripts |

## NOTES

- API docs: http://localhost:8000/api/doc.html
- Druid monitor: http://localhost:8000/api/druid (admin/admin123)