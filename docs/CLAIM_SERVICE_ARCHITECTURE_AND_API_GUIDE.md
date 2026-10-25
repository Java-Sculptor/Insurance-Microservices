# Claims Management Service (`claim-service`) — Architecture & API Reference

> **Service Name**: `claim-service`  
> **Port**: `8089`  
> **Status**: Successfully Compiled & Ready  
> **BRD Reference**: Section 10 (Pages 141–160)

---

## 1. Architectural Summary

`claim-service` is an autonomous microservice responsible for the complete insurance claim lifecycle:
- **Claim Registration**: Validates policy eligibility, coverage windows, and premium continuity via OpenFeign.
- **Document Management**: Collects supporting bills, FIRs, discharge summaries, and records verification status.
- **Surveyor Assignment**: Assigns licensed loss assessors, records inspection dates, and stores findings reports.
- **Fraud Investigation**: Flags early/suspicious claims and manages forensic investigation workflows.
- **Claim Assessment Engine**: Evaluates net payable payout using the formula:
  $$\text{Net Settlement} = \text{Assessed Amount} - \text{Deductible} - \text{Depreciation} - \text{Non-Payable}$$
  Capped at the policy's **Sum Insured**.
- **Settlement & Payout**: Managerial approval gate, release of funds (NEFT, RTGS, IMPS, UPI, Cheque), and audit tracking.

---

## 2. All 17 Implemented REST API Endpoints

### Core Claims Management
* `POST /api/v1/claims`: Register new claim (Validates active policy & no overdue premiums).
* `GET /api/v1/claims`: List all claims (Optional query param: `?status=REGISTERED|APPROVED|etc`).
* `GET /api/v1/claims/{id}`: Retrieve comprehensive claim details.
* `PUT /api/v1/claims/{id}`: Update claim information (estimated loss, description).
* `DELETE /api/v1/claims/{id}`: Cancel unverified claim (Only if status is `REGISTERED`).

### Document Verification
* `POST /api/v1/claims/{id}/documents`: Upload document reference.
* `GET /api/v1/claims/{id}/documents`: List all uploaded documents for a claim.
* `PUT /api/v1/claims/documents/{docId}/verify`: Update document status (`VERIFIED` or `REJECTED`).

### Surveyor Loss Assessment
* `POST /api/v1/claims/{id}/survey`: Assign surveyor to inspect loss on-site.
* `GET /api/v1/claims/{id}/survey`: View survey details for a claim.
* `PUT /api/v1/surveys/{surveyId}`: Submit surveyor report and inspection findings.

### Fraud Investigation
* `POST /api/v1/claims/{id}/investigation`: Assign forensic investigator.
* `PUT /api/v1/investigations/{id}`: Submit fraud report and conclusions.
* `GET /api/v1/claims/{id}/investigation`: View investigation details for a claim.

### Financial Assessment Engine
* `POST /api/v1/claims/{id}/assessment`: Compute net payable amount based on deductions and sum insured limit.
* `GET /api/v1/claims/{id}/assessment`: Retrieve assessment breakdown.

### Approval & Payout Settlement
* `PUT /api/v1/claims/{id}/approve`: Managerial approval of claim payout.
* `PUT /api/v1/claims/{id}/reject`: Rejection of claim with mandatory reason.
* `POST /api/v1/claims/{id}/settlement`: Process bank payout (generates UTR/transaction reference).
* `GET /api/v1/claims/{id}/settlement`: View settlement confirmation and payment status.

---

## 3. Database Schema (INS047 – INS053)

The service maps directly to the PAS enterprise tables:
1. `claim_master` (`INS047`)
2. `claim_documents` (`INS048`)
3. `survey_assignment` (`INS049`)
4. `investigation` (`INS050`)
5. `claim_assessment` (`INS051`)
6. `claim_settlement` (`INS052`)
7. `claim_history` (`INS053`)

---

## 4. Inter-Service Feign Clients
- **`PolicyClient`**: Communicates with `policy-service` on port `8087` to verify policy state, term dates, and coverage limits. Includes circuit-breaker fallback.
- **`PremiumClient`**: Communicates with `premium-service` on port `8087` to ensure all scheduled installments are up to date and that no `OVERDUE` premiums exist before a claim is registered. Includes circuit-breaker fallback.
