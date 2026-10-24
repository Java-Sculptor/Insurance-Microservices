# Insurance Management – Claim Management updates

## Implemented
- Added Investigation REST endpoints and service/repository flow.
- Added validated `InvestigationRequest` and enum-typed `InvestigationResponse`.
- Added `ApiResponse<T>` envelopes to the new Investigation APIs and eligibility endpoint.
- Added OpenFeign contracts for Policy and Premium Schedule services.
- Claim registration now checks policy/customer ownership, active status, effective/expiry dates, premium status, coverage, waiting period and remaining claim limit before saving.
- Added claim-specific exceptions and a global exception handler.
- Added SLF4J logs and rolling file logging at `logs/insurance-management.log`.
- Corrected claim-document rejection so a rejected document is stored as `REJECTED`.
- Corrected repository status parameter to use `ClaimStatus` enum.
- Fixed an incomplete `if (ClaimStatus.)` statement in assessment service and corrected document verification prerequisite.
- Database and mail credentials are now read from environment variables rather than embedded in source configuration.

## Required configuration
Set these environment variables before starting the service:
- `DB_USERNAME` (defaults to `root`)
- `DB_PASSWORD` (required for your local MySQL)
- `POLICY_SERVICE_URL` (defaults to `http://localhost:8082`)
- `PREMIUM_SERVICE_URL` (defaults to `http://localhost:8083`)
- `MAIL_USERNAME` / `MAIL_PASSWORD` only if email is enabled

The Policy service's `/api/v1/policies/{policyId}/claim-eligibility` response must include `customerId` in addition to policy status, dates, coverage, waiting-period and remaining-limit fields. This is required to validate that the claimant owns the submitted policy. Ensure the Premium service exposes `GET /api/v1/premiums/policy/{policyId}/claim-eligibility` and returns `premiumUpToDate`, `outstandingAmount`, and `lastPaymentStatus`.

## Investigation endpoints
- `POST /api/v1/claims/{claimId}/investigations` – assign/start investigation.
- `GET /api/v1/claims/{claimId}/investigations` – list investigations for a claim.
- `PATCH /api/v1/claims/{claimId}/investigations/{investigationId}/report` – submit report path using JSON: `{"reportPath":"uploads/reports/report.pdf"}`.

An investigation report moves the claim to `INVESTIGATION_COMPLETED`; this is represented as a Java enum value. The claim assessment/approval/settlement lifecycle remains separate.

## Build verification
The Maven wrapper could not download its Maven distribution in this environment because network access to Maven Central was unavailable. Please run `mvn test` (or `mvnw.cmd test` on Windows) locally after configuring the environment variables and checking the Policy/Premium service response contracts.
