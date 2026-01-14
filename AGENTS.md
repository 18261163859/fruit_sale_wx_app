# Repository Guidelines

## Project Structure & Module Organization
`backend/` hosts the Spring Boot API in `src/main/java/com/fruit/sale` with configs in `src/main/resources` and starter tests under `src/test`. `admin_web/` is the Vite/Vue admin console; core logic sits in `src/api`, shared state in `src/stores`, and routed pages in `src/views`. `mini_app/` contains the WeChat Mini Program organized per feature (`api/`, `utils/`, `pages/`), while root-level SQL files (`database_design.sql`, `init_data.sql`, `fix_*.sql`) keep database changes consistent across clients.

## Build, Test & Development Commands
- Backend: `mvn spring-boot:run` for dev, `mvn clean package -DskipTests` for a deployable jar, `./start.sh` to run the full clean-build-launch check, and `mvn test` before pushing.
- Admin Web: In `admin_web`, run `npm install`, `npm run dev` for hot reload, `npm run build` for production output, and `npm run preview` for a static smoke test.
- Mini App: Open `/mini_app` in WeChat Developer Tools, update `utils/request.js` with your backend host, and use **Compile** for iterative development.

## Coding Style & Naming Conventions
Backend code uses 4-space indentation, PascalCase classes, camelCase members, and Lombok for boilerplate; expose REST endpoints with plural paths (for example, `/orders`). Vue files use 2-space indentation, PascalCase single-file components, and kebab-case directories; keep Pinia stores typed under `src/stores`. Mini Program modules follow camelCase functions and align folder names with routes (for example, `pages/order-detail`).

## Testing Guidelines
Spring Boot tests run on JUnit 5; place new cases under `backend/src/test/java`, prefer slice tests where possible, and stub collaborators with Mockito. Front-end stacks lack automation for now—note manual QA steps in the PR and keep fresh service logic near 70% coverage via targeted assertions.

## Commit & Pull Request Guidelines
With no published Git history, adopt Conventional Commits (`feat: agent settlement API`). Pull requests should summarise scope, list affected modules, flag config/schema updates, and attach validation proof (`mvn test`, UI screenshots, migration notes).

## Configuration & Data Tips
Keep local secrets in `backend/src/main/resources/application-dev.yml`; never commit production values. Update SQL seeds and hotfix scripts alongside entity changes. Align Mini Program endpoints in `utils/request.js` and refresh assets in `mini_app/images`. Leave generated output (`backend/target`, `admin_web/dist`, `logs/`) untracked.
