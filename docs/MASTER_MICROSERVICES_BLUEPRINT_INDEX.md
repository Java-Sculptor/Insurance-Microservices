# Master Microservices Blueprint Index — Insurance Policy Administration System (PAS)

> **Status Overview**: 3 Services Implemented & Compiled (`BUILD SUCCESS`), 7 Services Completely Planned & Documented.  
> **Source of Truth**: Business Requirement Document (BRD) Modules 1–12.

---

## 1. Implemented & Verified Services (Codebase Ready)

| Microservice | Port | Database Schema | Core Responsibilities | Key Guides & Plan |
| :--- | :--- | :--- | :--- | :--- |
| **`premium-service`** | `8087` | `premium_service` | Multi-frequency schedules, dynamic late fees, 2 autonomous schedulers, idempotent payments, digital receipts, refund approval flow. | [Architecture Guide](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/PREMIUM_SERVICE_ARCHITECTURE_AND_SCHEDULER_GUIDE.md) |
| **`claim-service`** | `8089` | `claim_service` | 7 tables (INS047-INS053), 17 REST endpoints, surveyor assignment, fraud investigation, net settlement calculation engine, policy & premium continuity check. | [Architecture Guide](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/CLAIM_SERVICE_ARCHITECTURE_AND_API_GUIDE.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/CLAIM_SERVICE_ENDPOINTS_AND_SERVICEIMPL_DEEP_DIVE.md)<br>[Implementation Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/CLAIM_MANAGEMENT_IMPLEMENTATION_PLAN.md) |
| **`policy-service`** | `8090` | `policy_service` | 6 tables (INS030-INS035), 14 REST endpoints, policy number generation, versioned PDF schedules, endorsement engine, OpenFeign call to premium-service. | [Architecture Guide](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/POLICY_SERVICE_ARCHITECTURE_AND_API_GUIDE.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/POLICY_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md)<br>[Implementation Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/POLICY_ISSUANCE_IMPLEMENTATION_PLAN.md) |

---

## 2. Planned Services (Ready for Implementation)

| Microservice | Assigned Port | Planned Schema | Core Responsibilities & Business Logic Beyond CRUD | Implementation Plan |
| :--- | :--- | :--- | :--- | :--- |
| **`customer-service`** | `8082` | `customer_service` | Tables INS001–INS005, 17 APIs, Fuzzy Deduplication Engine, Nominee 100% Share Validation, Minor protection guards. | [Customer Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/CUSTOMER_SERVICE_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/CUSTOMER_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`kyc-service`** | `8083` | `kyc_service` | Tables INS006–INS010, 14 APIs, Verhoeff Checksum Aadhaar verification, PAN regex check, Central CKYC Registry integration, Compliance Gate. | [KYC Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/KYC_SERVICE_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/KYC_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`product-service`** | `8084` | `product_service` | Tables INS011–INS017, 18 APIs, Immutable Product Versioning, Dynamic Eligibility Matrix, Rider mutual exclusions. | [Product Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/PRODUCT_SERVICE_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/PRODUCT_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`quote-service`** | `8085` | `quote_service` | Tables INS018–INS023, 12 APIs, Actuarial Premium Calculation Engine, Risk Scoring Matrix (0-100), 30-Day Legal Price Lock. | [Quote Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/QUOTE_SERVICE_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/QUOTE_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`underwriting-service`** | `8086` | `underwriting_service` | Tables INS024–INS029, 14 APIs, Straight-Through Processing (STP) Auto-approval, BMI math & loading engine, Human Life Value limits. | [Underwriting Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/UNDERWRITING_SERVICE_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/UNDERWRITING_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`policy-servicing-service`** | `8091` | `servicing_service` | Tables INS041–INS046, 12 APIs, Non-financial & financial endorsement splitting, Before/After audit snapshots, Free-look cancellation refund trigger. | [Servicing Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/POLICY_SERVICING_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/POLICY_SERVICING_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`reports-service`** | `8092` | `reports_service` | Tables INS054–INS058, 16 APIs, Loss Ratio & Collection Efficiency formulas, Big-data streaming export (PDF/Excel/CSV), Automated cron schedules. | [Reports Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/REPORTS_SERVICE_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/REPORTS_SERVICE_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |
| **`admin-service`** | `8080` | `admin_service` | Tables INS059–INS068, 20 APIs, JWT Authentication & Refresh tokens, BCrypt hashing, Brute-force lockout (5 attempts), RBAC permissions. | [Admin/Security Plan](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/ADMIN_SECURITY_IMPLEMENTATION_PLAN.md)<br>[Deep Dive & Story](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/ADMIN_SECURITY_ENTITIES_AND_SERVICEIMPL_DEEP_DIVE.md) |

---

## 3. Presentation & Cross-Service Guides

* **[Module-by-Module Presentation & Defense Guide](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/MODULE_BY_MODULE_PRESENTATION_AND_DEFENSE_GUIDE.md)**: Script, elevator pitches, and defense strategies explaining why simple CRUD is only 15% of the system.
* **[Insurance Ecosystem Modules & Premium Integration Guide](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/docs/INSURANCE_ECOSYSTEM_MODULES_AND_PREMIUM_INTEGRATION_GUIDE.md)**: Deep dive into inter-service choreography, OpenFeign, and sequence diagrams.
