# Policy Issuance & Administration Service (`policy-service`) — Architecture & API Reference

> **Service Name**: `policy-service`  
> **Port**: `8090`  
> **Status**: Compiled & Verified (`BUILD SUCCESS`)  
> **BRD Reference**: Section 7 (Pages 94–109)

---

## 1. Architectural Summary

`policy-service` binds legal insurance contracts and oversees policy governance:
- **Policy Contract Issuance**: Receives approved quotes from Underwriting, validates dates, generates enterprise policy numbers (`{PROD}-{STATE}-{YEAR}-{SEQ}`), and stores coverages & riders.
- **Inter-Service Coordination**: Automatically calls `premium-service: POST /api/v1/premium-schedules` via OpenFeign to construct the installment schedule.
- **State Machine Integrity**: Policies start as `PENDING_PAYMENT` and transition to `ACTIVE` only upon initial premium payment confirmation.
- **Policy Endorsement Engine**: Processes financial and non-financial modifications, preserves before/after snapshot audits (`old_value` vs `new_value`), and supports managerial approval.
- **Digital Policy Schedule**: Generates and version-controls legal PDF contracts (`version_no`).

---

## 2. All 14 REST API Endpoints

### Core Policy Lifecycle
* `POST /api/v1/policies`: Issue new insurance policy from an approved quote.
* `GET /api/v1/policies`: List policies (Filterable by `?status=&customerId=`).
* `GET /api/v1/policies/{id}`: Retrieve complete policy details, coverage, riders, and status.
* `PUT /api/v1/policies/{id}`: Update policy dates or administrative remarks.
* `DELETE /api/v1/policies/{id}`: Cancel policy (before activation or during free-look).

### Policy State Transitions
* `PUT /api/v1/policies/{id}/activate`: Activate policy (called by `premium-service` upon payment).
* `PUT /api/v1/policies/{id}/suspend`: Suspend policy (due to overdue premium non-payment).
* `PUT /api/v1/policies/{id}/expire`: Mark policy expired at term completion.

### Document Management
* `GET /api/v1/policies/{id}/document`: Retrieve latest versioned policy PDF contract metadata.
* `POST /api/v1/policies/{id}/document/regenerate`: Recompile and bump document version (`v1` -> `v2`).

### Endorsements
* `POST /api/v1/policies/{id}/endorsements`: Submit endorsement request (e.g., address, nominee, coverage).
* `GET /api/v1/policies/{id}/endorsements`: List all endorsements for a policy.
* `PUT /api/v1/endorsements/{endorsementId}/approve`: Approve endorsement and regenerate contract.
* `PUT /api/v1/endorsements/{endorsementId}/reject`: Reject endorsement with mandatory remarks.

---

## 3. Database Schema (INS030 – INS035)

1. `policy` (`INS030`): Master contract record.
2. `policy_coverage` (`INS031`): Multi-coverage limits.
3. `policy_rider` (`INS032`): Bundled optional riders.
4. `policy_document` (`INS033`): PDF schedule version history.
5. `policy_endorsement` (`INS034`): Modification audit snapshots.
6. `policy_history` (`INS035`): Immutable activity logs.
