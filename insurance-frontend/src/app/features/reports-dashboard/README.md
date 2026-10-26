# Reports & Dashboard (TL module)

Scope: Angular screen for the existing `reports-dashboard-service` Spring Boot microservice.
This feature is reachable at `/admin/reports-dashboard` in the common app. It makes **no changes** to shared `theme/`, `auth/`, or other teams' features. Shared route already exists in `app.routes.ts`.

## API integration

Uses the environment's `apiBaseUrl` (development value `/api/v1`) and the Angular development proxy to `http://127.0.0.1:8095`.

- `GET /api/v1/dashboard/summary` — KPI cards
- `GET /api/v1/dashboard/charts` — policies by type, claims by status, monthly premium and issuance charts
- `GET /api/v1/reports/policies|agents|claims|premiums|renewals` — searchable paginated tables
- `GET /api/v1/reports/export/csv?type=POLICY|AGENT|CLAIM|PREMIUM|RENEWAL` — downloads CSV with applied filters

The UI deliberately has no invented dummy data: when Spring Boot is stopped, it displays a useful API-offline state with a Retry action.

## Local testing

1. Run local MySQL and `reports-dashboard-service` in IntelliJ with your **local** profile (`app.demo.seed=true`) on port 8095.
2. From `insurance-frontend/` run `npm ci` (first time) then `npm start`.
3. Open `http://localhost:4200/admin/reports-dashboard`.
4. Check Overview KPI cards, chart breakdowns, Reports tab, report type switching, filters, next/previous, CSV download.
5. Stop backend and confirm an offline message is displayed; restart and Retry.
6. Run `npm run build` to validate before committing.

## Team integration / security

- Make changes only on `frontend/reports-dashboard` branched from `develop`; use a Pull Request into `develop`.
- Angular role selection is currently a UI preview, **not real authorization**. All report endpoints currently require proper server-side authorization before production use.
- Dashboard totals are portfolio-wide, **not customer-specific**; do not show them as a customer's personal stats.
- The old AWS EC2 instance is stopped; for local testing use your Windows IntelliJ backend. Do not need AWS running.
