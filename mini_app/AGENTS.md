# MINI APP MODULE

**Platform:** WeChat Mini Program

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

## CONVENTIONS

- camelCase functions
- Route-aligned folder naming (`pages/order-detail/`)
- ES6 enabled, minification active
- WeChat trial lib version

## ANTI-PATTERNS

- **No automated tests**
- **Manual testing only** via WeChat DevTools
- **No CI pipeline** configured

## COMMANDS

- Open in WeChat Developer Tools
- Update `utils/request.js` backend host
- Use Compile for iterative development