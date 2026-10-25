# Implementation Plan: Policy Issuance Module (`policy-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 7 (Pages 94–109)  
> **Service Name**: `policy-service` (or `policy-issuance-service`)  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/policy-service`  
> **Assigned Port**: `8090` (or `8087`)  
> **Status**: Ready for Review & Execution

---

## 1. Architectural Overview & Domain Role

The **Policy Issuance Module** converts an approved underwriting proposal into an official, legally binding insurance contract. It creates the policy master record, generates formatted enterprise policy numbers, triggers initial premium schedule creation in `premium-service`, compiles the digital policy PDF schedule, and manages post-issuance endorsements.

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                              POLICY LIFECYCLE STATE MACHINE                            │
└────────────────────────────────────────────────────────────────────────────────────────┘

 [Underwriting Service]          [Premium Service]              [Policyholder / Servicing]
  Proposal Approved               Initial Payment Success        Endorsement or Lapse
         │                               │                               │
         ▼                               ▼                               ▼
  ┌──────────────┐               ┌──────────────┐               ┌──────────────────┐
  │    DRAFT     │ ────────────> │PENDING_PAYMENT│ ────────────> │      ACTIVE      │
  └──────────────┘               └──────┬───────┘               └────────┬─────────┘
                                        │                                │
                                        ▼                                ▼
                                 ┌──────────────┐               ┌──────────────────┐
                                 │  CANCELLED   │ <──────────── │SUSPENDED / EXPIRED
                                 └──────────────┘               └──────────────────┘
```

---

## 2. Database Design & Entity Model (Tables INS030 – INS035)

The service maps directly to the 6 core tables specified in BRD Section 7:

### Table INS030: `policy`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `policy_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `policy_number` | `VARCHAR(50)` | `UNIQUE, NOT NULL` | Business legal identifier (e.g. `HLT-MH-2026-100245`) |
| `quote_id` | `BIGINT` | `NOT NULL, INDEX` | Foreign reference to approved Quote |
| `customer_id` | `BIGINT` | `NOT NULL, INDEX` | Foreign reference to Policyholder |
| `product_id` | `BIGINT` | `NOT NULL, INDEX` | Insurance Product ID |
| `issue_date` | `DATE` | `NOT NULL` | Date when policy was generated |
| `effective_date`| `DATE` | `NOT NULL` | Coverage start date |
| `expiry_date` | `DATE` | `NOT NULL` | Coverage expiration date |
| `premium_frequency` | `VARCHAR(30)` | `NOT NULL` | `MONTHLY`, `QUARTERLY`, `HALF_YEARLY`, `YEARLY`, `SINGLE` |
| `sum_insured` | `DECIMAL(15,2)` | `NOT NULL` | Total coverage limit |
| `final_premium`| `DECIMAL(15,2)` | `NOT NULL` | Annual/term final premium |
| `policy_status` | `ENUM` | `NOT NULL` | `DRAFT`, `PENDING_PAYMENT`, `ACTIVE`, `SUSPENDED`, `CANCELLED`, `EXPIRED`, `CLOSED` |
| `created_at` | `DATETIME` | `NOT NULL` | Audit timestamp |
| `updated_at` | `DATETIME` | `NOT NULL` | Audit timestamp |

### Table INS031: `policy_coverage`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `coverage_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `policy_id` | `BIGINT` | `FK(policy), NOT NULL` | Associated policy |
| `coverage_name`| `VARCHAR(150)` | `NOT NULL` | e.g. Base Hospitalization, Third-Party Liability |
| `sum_insured` | `DECIMAL(15,2)` | `NOT NULL` | Limit for this specific cover |
| `coverage_start_date` | `DATE` | `NOT NULL` | Start date |
| `coverage_end_date` | `DATE` | `NOT NULL` | End date |

### Table INS032: `policy_rider`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `policy_rider_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `policy_id` | `BIGINT` | `FK(policy), NOT NULL` | Associated policy |
| `rider_id` | `BIGINT` | `NOT NULL` | Master rider reference |
| `rider_name` | `VARCHAR(150)` | `NOT NULL` | Critical Illness, Accidental Death, etc. |
| `rider_premium` | `DECIMAL(15,2)` | `NOT NULL` | Cost of the rider |

### Table INS033: `policy_document`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `document_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `policy_id` | `BIGINT` | `FK(policy), NOT NULL` | Associated policy |
| `document_name`| `VARCHAR(200)` | `NOT NULL` | e.g. `Policy_Schedule_HLT-MH-2026-100245.pdf` |
| `file_path` | `VARCHAR(500)` | `NOT NULL` | Document storage URL / local path |
| `version_no` | `INT` | `DEFAULT 1` | Increments on endorsements |
| `generated_date` | `DATETIME` | `NOT NULL` | Generation timestamp |

### Table INS034: `policy_endorsement`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `endorsement_id`| `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `policy_id` | `BIGINT` | `FK(policy), NOT NULL` | Associated policy |
| `endorsement_number` | `VARCHAR(50)` | `UNIQUE, NOT NULL`| e.g. `END-2026-000412` |
| `endorsement_type` | `VARCHAR(100)` | `NOT NULL` | `ADDRESS_CHANGE`, `NOMINEE_CHANGE`, `COVERAGE_INCREASE`, etc. |
| `old_value` | `TEXT` | `NOT NULL` | Previous JSON snapshot |
| `new_value` | `TEXT` | `NOT NULL` | Updated JSON snapshot |
| `approved_by` | `BIGINT` | `NOT NULL` | Approving employee/officer ID |
| `approval_status` | `ENUM` | `DEFAULT 'PENDING'` | `PENDING`, `APPROVED`, `REJECTED` |
| `endorsement_date` | `DATETIME` | `NOT NULL` | Effective timestamp |

### Table INS035: `policy_history`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `history_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `policy_id` | `BIGINT` | `FK(policy), NOT NULL` | Associated policy |
| `action` | `VARCHAR(100)` | `NOT NULL` | `Issued`, `Activated`, `Endorsed`, `Suspended` |
| `performed_by` | `BIGINT` | `NOT NULL` | Employee or System User ID |
| `action_date` | `DATETIME` | `NOT NULL` | Timestamp |
| `remarks` | `TEXT` | `NULL` | Contextual notes |

---

## 3. All 14 RESTful API Endpoints to Implement

### Group 1: Core Policy Lifecycle APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/policies` | Issue new insurance policy from approved quote | Underwriter / Operations |
| `GET` | `/api/v1/policies` | List all policies (supports filters: `?status=&customerId=`) | All Roles |
| `GET` | `/api/v1/policies/{id}` | Get complete policy details (terms, coverage, riders, dates) | All Roles |
| `PUT` | `/api/v1/policies/{id}` | Update policy details | Operations Executive |
| `DELETE`| `/api/v1/policies/{id}` | Cancel policy (pre-activation or free-look) | Branch Manager |

### Group 2: Policy Document APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/policies/{id}/document` | Retrieve policy PDF contract metadata / download URL | Customer, Agent |
| `POST` | `/api/v1/policies/{id}/document/regenerate` | Re-compile and generate a new versioned PDF schedule | Operations / Admin |

### Group 3: Policy Endorsement APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/policies/{id}/endorsements` | Submit a policy endorsement request | Customer, Agent |
| `GET` | `/api/v1/policies/{id}/endorsements` | List all endorsements for a policy | Operations Executive |
| `PUT` | `/api/v1/endorsements/{endorsementId}/approve` | Approve endorsement and apply updates to policy | Underwriter / Manager |
| `PUT` | `/api/v1/endorsements/{endorsementId}/reject` | Reject endorsement with remarks | Underwriter / Manager |

### Group 4: Policy State Transition APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `PUT` | `/api/v1/policies/{id}/activate` | Transition status to `ACTIVE` (invoked upon payment) | `premium-service` |
| `PUT` | `/api/v1/policies/{id}/suspend` | Transition status to `SUSPENDED` (unpaid overdue) | Overdue Scheduler / Finance |
| `PUT` | `/api/v1/policies/{id}/cancel` | Transition status to `CANCELLED` | Customer / Admin |
| `PUT` | `/api/v1/policies/{id}/expire` | Transition status to `EXPIRED` (term completion) | Expiry Scheduler / System |

---

## 4. Key Business Logic Beyond Simple CRUD

1. **Policy Number Generator Engine (FR-POL-002)**:
   - Must generate structured, non-duplicable business identifiers:
     $$\text{Format}: \{\text{PROD\_CODE}\}-\{\text{STATE\_CODE}\}-\{\text{YEAR}\}-\{\text{SEQUENCE}\}$$
     *(e.g., `HLT-MH-2026-000100245` or `LIFE-DL-2026-000088123`)*
2. **Coordinated Schedule Creation via OpenFeign**:
   - Immediately upon creating a policy in `DRAFT` or `PENDING_PAYMENT` status, `policy-service` makes an inter-service call to `premium-service`:
     `POST http://premium-service/api/v1/premium-schedules`
     passing `policyId`, `policyTermMonths`, `premiumFrequency`, `startDate`, and `finalPremium`.
3. **State Machine Integrity Gate**:
   - Direct manual activation is disallowed. A policy can only move to `ACTIVE` when `premium-service` calls `PUT /policies/{id}/activate` after confirming receipt of Installment #1.
4. **Endorsement Snapshot Engine**:
   - Financial endorsements (Coverage Increase, Rider Addition) record the previous coverage state and automatically request `premium-service` to adjust future installment amounts.
5. **Digital PDF Schedule Assembly**:
   - Populates policyholder details, nominee mappings, coverage tables, exclusions, and digital signature metadata into a standardized PDF artifact.

---

## 5. Step-by-Step Implementation Roadmap

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      IMPLEMENTATION EXECUTION PHASES                    │
├─────────────────────────────────────────────────────────────────────────┤
│ Phase 1: Project Setup & Configuration                                   │
│   • Directory: /Users/apple/Workspace/team 11/Insurance-Microservices/   │
│                policy-service                                           │
│   • Port: 8090 (or configured port)                                     │
│   • MySQL datasource with Aiven cluster (policy_service database)       │
│                                                                         │
│ Phase 2: Domain Entities & Enums                                        │
│   • 6 JPA Entities (INS030 - INS035)                                    │
│   • Enums: PolicyStatus, EndorsementType, EndorsementStatus             │
│                                                                         │
│ Phase 3: DTOs & Validation Layer                                        │
│   • Request & Response DTOs with validation rules                       │
│                                                                         │
│ Phase 4: OpenFeign Clients                                              │
│   • PremiumClient: Calls POST /premium-schedules on issuance            │
│   • UnderwritingClient: Verifies proposal approval                      │
│   • CustomerClient: Retrieves customer profile details                  │
│                                                                         │
│ Phase 5: Service Layer Implementation                                   │
│   • PolicyNumberGenerator service                                       │
│   • PolicyIssuanceService (Coordination with Premium Service)           │
│   • PolicyEndorsementService (Snapshot & Approval workflow)             │
│   • PolicyDocumentService (PDF generation simulation & metadata)        │
│                                                                         │
│ Phase 6: REST Controllers & Exception Handling                          │
│   • PolicyController, DocumentController, EndorsementController         │
│   • GlobalExceptionHandler (Handling 404, 400, 422)                     │
│                                                                         │
│ Phase 7: Verification & Build                                           │
│   • Full ./mvnw clean compile verification (0 errors)                   │
└─────────────────────────────────────────────────────────────────────────┘
```
