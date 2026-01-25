# MINI APP MODULE

**Platform:** WeChat Mini Program
**AppID:** wxf7c1e0a5c304e714

## STRUCTURE

```
mini_app/
├── api/           # Backend API wrappers (9 modules)
├── utils/         # Utilities (request, theme, image, upload, storage)
├── pages/         # 18 feature pages
├── components/    # Shared components
└── styles/        # Page styles
```

## WHERE TO LOOK

| Task | Location |
|------|----------|
| API calls | `api/` (user.js, product.js, order.js, agent.js, cart.js, points.js, vip.js, address.js, config.js) |
| HTTP client | `utils/request.js` |
| Theme system | `utils/theme.js` (3 themes: regular, VIP, New Year) |
| File upload | `utils/upload.js` |
| Auth | `utils/request.js` (token headers) |
| Login/Auth flow | `app.js` |

## ENTRY POINT

- `app.js` - WeChat lifecycle, theme initialization, login checks

## CONVENTIONS

- camelCase functions
- Route-aligned folder naming (`pages/order-detail/`)
- ES6 enabled, minification active
- WeChat trial lib version
- Promise/async-await for API calls

## ANTI-PATTERNS (THIS MODULE)

**Critical:**
- **Hardcoded IPs**: `app.js` (line 14) and `utils/request.js` (line 5) use 127.0.0.1

**High:**
- **No automated tests**
- **Manual testing only** via WeChat DevTools
- **No CI pipeline** configured

**Medium:**
- **Incomplete pages**: Only login, index, cart fully implemented; 15 others have file structure only

## COMMANDS

- Open in WeChat Developer Tools
- Update `utils/request.js` backend host before deployment
- Use Compile for iterative development

## THEME SYSTEM

Three themes based on user type and date:
- **Regular**: blue/white (#1989fa)
- **VIP**: black/gold (#d4af37, #0a0a0a)
- **New Year**: red/gold (#ff4444, #d4af37) - auto-enabled during lunar new year period

## CONFIGURATION

| File | Purpose |
|------|---------|
| `project.config.json` | WeChat appid, compile settings |
| `app.json` | Routes, tabBar, window styles |
| `utils/request.js` | Base URL, interceptors, auth |

## NOTES

- Backend URL: `http://localhost:8000` (dev) or configurable
- Token auth via `Authorization` header
- 18 pages with 4-tab navigation (home/category/cart/profile)
- Pull-down refresh enabled globally