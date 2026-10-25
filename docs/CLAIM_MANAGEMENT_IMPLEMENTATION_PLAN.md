# Implementation Plan: Claims Management Module (`claims-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 10 (Pages 141–160)  
> **Service Name**: `claims-service` (Port: `8088` / `8089`)  
> **Status**: Ready for Execution

---

## 1. Architectural Overview & Domain Role

The **Claims Management Module** is the critical fulfillment engine of the Insurance Policy Administration System (PAS). When an insured event occurs (e.g., motor collision, hospitalization, property damage, loss of life), this service coordinates the entire lifecycle from registration to financial settlement.

```
┌────────────────────────────────────────────────────────────────────────────────────────┐
│                              CLAIMS LIFECYCLE STATE MACHINE                            │
└────────────────────────────────────────────────────────────────────────────────────────┘

 [Customer/Agent]             [Claims Executive]             [Surveyor / Investigator]
  Register Claim               Verify Policy & Docs            Damage Assessment
         │                              │                              │
         ▼                              ▼                              ▼
  ┌──────────────┐              ┌──────────────┐              ┌──────────────────┐
  │  REGISTERED  │ ───────────> │   PENDING    │ ───────────> │   UNDER SURVEY   │
  └──────────────┘              │ VERIFICATION │              │/ INVESTIGATION   │
                                └──────┬───────┘              └────────┬─────────┘
                                       │                               │
                                       ▼                               ▼
                                ┌──────────────┐              ┌──────────────────┐
                                │   REJECTED   │ <─────────── │ CLAIM ASSESSMENT │
                                └──────────────┘              └────────┬─────────┘
                                                                       │
                                [Finance Officer]             [Claims Manager]
                                 Release Payment               Approve / Reject
                                       │                               │
                                       ▼                               ▼
                                ┌──────────────┐              ┌──────────────────┐
                                │   SETTLED    │ <─────────── │     APPROVED     │
                                └──────────────┘              └──────────────────┘
```

---

## 2. Database Design & Entity Model (Tables INS047 – INS053)

The module requires **7 core database tables** adhering to the PAS naming conventions:

### Table INS047: `claim_master`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `claim_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_number` | `VARCHAR(30)` | `UNIQUE, NOT NULL` | Business identifier (e.g. `CLM-2026-000100`) |
| `policy_id` | `BIGINT` | `NOT NULL, INDEX` | Foreign key reference to Policy |
| `customer_id` | `BIGINT` | `NOT NULL, INDEX` | Foreign key reference to Customer |
| `claim_type` | `VARCHAR(50)` | `NOT NULL` | `HEALTH`, `MOTOR`, `LIFE`, `TRAVEL`, `PROPERTY` |
| `incident_date` | `DATE` | `NOT NULL` | Date when loss/damage occurred |
| `claim_date` | `DATE` | `NOT NULL` | Date when claim was submitted |
| `estimated_loss`| `DECIMAL(15,2)`| `NOT NULL` | Initial estimated loss reported by claimant |
| `claim_status` | `ENUM` | `NOT NULL` | `REGISTERED`, `PENDING_VERIFICATION`, `UNDER_SURVEY`, `UNDER_INVESTIGATION`, `APPROVED`, `REJECTED`, `SETTLED` |
| `created_at` | `DATETIME` | `NOT NULL` | Audit timestamp |
| `updated_at` | `DATETIME` | `NOT NULL` | Audit timestamp |

### Table INS048: `claim_documents`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `document_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_id` | `BIGINT` | `FK(claim_master), NOT NULL` | Associated claim |
| `document_name`| `VARCHAR(150)` | `NOT NULL` | e.g. Hospital Bill, FIR, RC Book, Death Cert |
| `file_path` | `VARCHAR(500)` | `NOT NULL` | Storage URL / S3 / Local directory path |
| `verification_status` | `ENUM` | `DEFAULT 'PENDING'` | `PENDING`, `VERIFIED`, `REJECTED` |
| `uploaded_at` | `DATETIME` | `NOT NULL` | Upload timestamp |

### Table INS049: `survey_assignment`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `survey_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_id` | `BIGINT` | `FK(claim_master), NOT NULL` | Associated claim |
| `surveyor_id` | `BIGINT` | `NOT NULL` | Assigned surveyor employee ID |
| `assigned_date`| `DATETIME` | `NOT NULL` | Assignment timestamp |
| `inspection_date`| `DATETIME` | `NULL` | On-site inspection timestamp |
| `report_path` | `VARCHAR(500)` | `NULL` | Surveyor loss assessment report PDF |
| `survey_status`| `ENUM` | `DEFAULT 'ASSIGNED'` | `ASSIGNED`, `COMPLETED` |

### Table INS050: `investigation`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `investigation_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_id` | `BIGINT` | `FK(claim_master), NOT NULL` | Associated claim |
| `investigator_id` | `BIGINT` | `NOT NULL` | Fraud investigator employee ID |
| `investigation_reason` | `TEXT` | `NOT NULL` | Fraud indicators, early claim flag, mismatch |
| `report_path` | `VARCHAR(500)` | `NULL` | Forensic / Investigation report path |
| `investigation_status` | `ENUM` | `DEFAULT 'PENDING'` | `PENDING`, `COMPLETED` |

### Table INS051: `claim_assessment`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `assessment_id`| `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_id` | `BIGINT` | `FK(claim_master), UNIQUE` | 1-to-1 assessment per claim |
| `assessed_amount` | `DECIMAL(15,2)` | `NOT NULL` | Verified total bill / loss |
| `deductible` | `DECIMAL(15,2)` | `DEFAULT 0.00` | Mandatory policy deductible |
| `depreciation` | `DECIMAL(15,2)` | `DEFAULT 0.00` | Vehicle / asset depreciation deduction |
| `non_payable` | `DECIMAL(15,2)` | `DEFAULT 0.00` | Non-medical / non-covered expenses |
| `net_payable` | `DECIMAL(15,2)` | `NOT NULL` | **$\text{Assessed} - \text{Deductible} - \text{Depr} - \text{NonPayable}$** |

### Table INS052: `claim_settlement`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `settlement_id`| `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_id` | `BIGINT` | `FK(claim_master), NOT NULL` | Associated claim |
| `approved_amount`| `DECIMAL(15,2)` | `NOT NULL` | Approved payout amount |
| `payment_mode` | `VARCHAR(50)` | `NOT NULL` | `NEFT`, `RTGS`, `IMPS`, `UPI`, `CHEQUE` |
| `transaction_reference` | `VARCHAR(100)` | `UNIQUE, NOT NULL` | Bank UTR / Transaction reference |
| `settlement_date` | `DATETIME` | `NOT NULL` | Payout timestamp |
| `settlement_status` | `ENUM` | `NOT NULL` | `PENDING`, `PAID`, `FAILED` |

### Table INS053: `claim_history`
| Column Name | Data Type | Constraint | Description |
| :--- | :--- | :--- | :--- |
| `history_id` | `BIGINT` | `PK, AUTO_INCREMENT` | Internal primary key |
| `claim_id` | `BIGINT` | `FK(claim_master), NOT NULL` | Associated claim |
| `action` | `VARCHAR(100)` | `NOT NULL` | Action taken (e.g. Registered, Survey Assigned) |
| `performed_by` | `BIGINT` | `NOT NULL` | Employee or Customer ID |
| `action_date` | `DATETIME` | `NOT NULL` | Audit timestamp |
| `remarks` | `TEXT` | `NULL` | Justification or comments |

---

## 3. Complete RESTful API Endpoints Specification

All endpoints follow the base path: `/api/v1`

### Group 1: Core Claim APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/claims` | Register new insurance claim | Customer, Agent |
| `GET` | `/api/v1/claims` | List all claims (with filters by status, type, date) | Claims Executive, Admin |
| `GET` | `/api/v1/claims/{id}` | Get complete claim details with docs, assessment & survey | All Roles |
| `PUT` | `/api/v1/claims/{id}` | Update claim details (description, estimated loss) | Claims Executive |
| `DELETE` | `/api/v1/claims/{id}` | Cancel claim (Only if status is `REGISTERED`) | Customer, Agent |

### Group 2: Document Verification APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/claims/{id}/documents` | Upload claim supporting documents (bills, FIR, reports) | Customer, Agent |
| `GET` | `/api/v1/claims/{id}/documents` | List all uploaded documents for a claim | Claims Executive |
| `PUT` | `/api/v1/claims/{id}/documents/{docId}/verify` | Verify or reject uploaded document | Claims Executive |

### Group 3: Surveyor APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/claims/{id}/survey` | Assign licensed surveyor for on-site inspection | Claims Executive |
| `GET` | `/api/v1/claims/{id}/survey` | View surveyor assignment details | Surveyor, Executive |
| `PUT` | `/api/v1/surveys/{surveyId}` | Submit surveyor inspection report & findings | Surveyor |

### Group 4: Fraud Investigation APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/claims/{id}/investigation` | Trigger fraud investigation | Claims Manager |
| `PUT` | `/api/v1/investigations/{id}` | Submit investigation findings & verdict | Investigator |

### Group 5: Assessment & Calculation APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/claims/{id}/assessment` | Calculate net payable amount using deductions formula | Claims Executive |
| `GET` | `/api/v1/claims/{id}/assessment` | View assessment breakdown | Claims Manager, Auditor |

### Group 6: Approval & Settlement APIs
| HTTP Method | Endpoint | Description | Role / Consumer |
| :--- | :--- | :--- | :--- |
| `PUT` | `/api/v1/claims/{id}/approve` | Approve claim for payout | Claims Manager |
| `PUT` | `/api/v1/claims/{id}/reject` | Reject claim with mandatory reason | Claims Manager |
| `POST` | `/api/v1/claims/{id}/settlement` | Process bank payout (NEFT/RTGS/IMPS/UPI) | Finance Officer |
| `GET` | `/api/v1/claims/{id}/settlement` | View settlement confirmation and UTR | All Roles |

---

## 4. Inter-Service Communication (OpenFeign Integration)

`claims-service` does NOT work in isolation. It relies on 3 other microservices:

```
┌────────────────────────────────────────────────────────┐
│                     claims-service                     │
└───────────────┬────────────────────────┬───────────────┘
                │                        │
       HTTP GET │               HTTP GET │
/api/v1/policies│               /api/v1/premium-schedules
          /{id} │                        │ /{policyId}
                ▼                        ▼
 ┌──────────────────────┐      ┌─────────────────────────┐
 │    policy-service    │      │     premium-service     │
 │ • Policy is ACTIVE?  │      │ • Any OVERDUE premiums? │
 │ • Within Term Dates? │      │ • Is coverage current?  │
 │ • Sum Insured Limit  │      │                         │
 └──────────────────────┘      └─────────────────────────┘
```

1. **`PolicyClient` (to `policy-service`)**:
   - Validates that `policyStatus == 'ACTIVE'`.
   - Validates that `incident_date >= policy.effectiveDate` and `incident_date <= policy.expiryDate`.
   - Verifies that claim payout does not exceed `sum_insured`.
2. **`PremiumClient` (to `premium-service`)**:
   - Fetches schedule: `GET /api/v1/premium-schedules/{policyId}`.
   - Enforces business rule: If any installment has status `OVERDUE`, claim registration/settlement is **blocked** until customer clears arrears.

---

## 5. Key Business Logic & Algorithms

### A. Mathematical Net Settlement Formula (FR-CLM-006):
```
Net Settlement = Assessed Amount - Policy Deductible - Depreciation - Non-Payable Expenses
```
* **Deductible**: Product-specific compulsory co-pay (e.g. ₹5,000).
* **Depreciation**: Applicable on motor metal/plastic parts (e.g. 5% to 50%).
* **Non-Payable**: Hospital administrative charges, gloves, extra consumables.
* **Cap Guard**: $\text{Net Settlement} \le \text{Policy Sum Insured}$.

### B. Automated Fraud Detection Triggers:
1. **Early Claim Alert**: If `incident_date - policy_effective_date < 30 days`.
2. **Spike Alert**: Customer has lodged $> 2$ claims in the trailing 12 months.
3. **High Value Gate**: If `estimated_loss > ₹1,00,000`, mandates `Claims Manager` dual-authorization and mandatory surveyor assignment.

---

## 6. Step-by-Step Implementation Roadmap for New Project

```
┌─────────────────────────────────────────────────────────────────────────┐
│                      IMPLEMENTATION EXECUTION PHASES                    │
├─────────────────────────────────────────────────────────────────────────┤
│ Phase 1: Project Skeleton & Configuration                                │
│   • Initialize Maven project (com.ampta:claims-service, Spring Boot 4.x)│
│   • Set server port (e.g. 8089) and configure MySQL datasource          │
│   • Configure OpenFeign & Eureka / LoadBalancer                         │
│                                                                         │
│ Phase 2: Domain Entities & Enums                                        │
│   • Create 7 JPA Entities (INS047 - INS053)                             │
│   • Create Enums (ClaimStatus, ClaimType, SettlementStatus, etc.)       │
│                                                                         │
│ Phase 3: DTOs & Validation Layer                                        │
│   • Request and Response DTOs with Jakarta validation constraints       │
│                                                                         │
│ Phase 4: Repositories & Feign Clients                                   │
│   • 7 JPA Repositories with custom query methods                        │
│   • Feign clients for PolicyService and PremiumService with fallbacks   │
│                                                                         │
│ Phase 5: Service Layer Implementation                                   │
│   • ClaimRegistrationService (Policy validation & duplicate check)      │
│   • ClaimDocumentService (Multipart upload & verification)              │
│   • ClaimSurveyService (Surveyor assignment & inspection)               │
│   • ClaimAssessmentService (Formula calculation engine)                 │
│   • ClaimSettlementService (Manager approval & bank payout)             │
│                                                                         │
│ Phase 6: REST Controllers & Exception Handling                          │
│   • ClaimController, DocumentController, SurveyController, etc.         │
│   • GlobalExceptionHandler (Handling 404, 400, 409, 422)                │
│                                                                         │
│ Phase 7: Build Verification & Documentation                             │
│   • Clean compile & unit test coverage                                 │
└─────────────────────────────────────────────────────────────────────────┘
```
