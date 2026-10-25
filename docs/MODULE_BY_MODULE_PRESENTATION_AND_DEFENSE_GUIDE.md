# Insurance PAS: Complete Module-by-Module Presentation & Technical Defense Guide

> **Purpose**: Master presentation handbook for team demos, project presentations, technical reviews, and engineering interviews.  
> **How to Use**: Read this guide before presenting the system. It equips you with the exact technical talking points, the philosophy behind CRUD vs. Business Logic, challenges faced, and how to defend each module like a seasoned engineer.

---

## Executive Summary: How to Frame Your Presentation

When presenting this project to interviewers, architects, or managers, **never frame it as a collection of simple database CRUD forms**. 

### The Core Theme to Open With:
> *"An Insurance Policy Administration System (PAS) is not an e-commerce catalog or a blog. An insurance policy is a regulated, legally binding financial contract. A single bug or unverified state change can lead to regulatory penalties from IRDAI, financial fraud, or unpaid legal claims. Therefore, our microservices architecture is built around strict state machines, actuarial calculation engines, compliance gates, autonomous background schedulers, and resilient inter-service communication via OpenFeign."*

---

## The Big Question: "Is CRUD Needed? And What Else Do We Do?"

Reviewers will frequently ask: *"Isn't this service just creating, updating, and deleting rows from MySQL?"*

### Your Answer:
1. **CRUD is just the baseline data persistence layer (about 15% of the codebase).** You need basic CRUD to create records (like a customer draft or product definition) and retrieve them for UI tables.
2. **Why Plain CRUD is Dangerous in Insurance:**
   - In standard CRUD, a client could send `PUT /policies/101` with `status: ACTIVE` without paying. In our system, **direct status mutation is blocked**. Status changes are strictly driven by verifiable domain events (e.g., `POST /payments` ➔ receipt issued ➔ transition event).
   - In standard CRUD, an installment could be deleted while claims are pending. In our system, **soft-deletes and immutable audit trails** prevent data destruction.
3. **What We Perform Beyond CRUD:**
   - **Actuarial Mathematical Engines** (Compound loading, risk scoring, multi-frequency premium splitting).
   - **Checksum & Algorithmic Validations** (Verhoeff Aadhaar verification, PAN structure compliance, 100% nominee share validation).
   - **Autonomous Background Schedulers** (Grace period expiration, late fee calculations, automated multi-stage reminders, payment reconciliation).
   - **State Machine Integrity** (Guarding entity lifecycles so steps cannot be skipped).
   - **Idempotency & Concurrency Locks** (Protecting against duplicate payment clicks and double-billing).
   - **Resilient Inter-Service Communication** (Declarative OpenFeign with circuit-breaker fallbacks).

---

## Module-by-Module Technical Breakdown

---

### Module 1: Customer Registration & Profile (`customer-service`)

#### 1. The Presentation Pitch:
> *"The Customer Service is the single source of truth for policyholder identity. It centralizes KYC-ready demographic records, maintains multi-address mapping, and ensures strict legal nominee distribution before any financial quote can be generated."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `customer` (`INS001`), `customer_address` (`INS002`), `nominee` (`INS003`), `customer_document` (`INS004`), `customer_contact` (`INS005`).
* **Roles**: Customer (Self-service), Agent (Onboarding), Administrator (Full view).
* **Domain Rule**: A customer must have a unique `Customer Code` (e.g., `CUST-2026-XXXX`).

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - `POST /customers`: Creates initial draft profile.
  - `GET /customers/{id}`: Fetches profile for portal display.
  - `PUT /customers/{id}`: Updates contact details.
* **Beyond CRUD (The Real Engineering)**:
  1. **Fuzzy Deduplication Engine**: Before inserting, checks `First Name + Last Name + DOB + Mobile + Email` to prevent duplicate profile creation across different regional branches.
  2. **Nominee 100% Share Validation**: In insurance law, nominee share percentages across all beneficiaries must equal exactly `100.00%`. The service rejects any payload where $\sum \text{Share} \ne 100\%$.
  3. **Minor Nominee Legal Guard**: If nominee age is under 18, the system strictly enforces the presence of an appointed legal guardian name and relationship.

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge**: Multiple nominees submitted in separate requests causing transient totals to not equal 100%.
  - **Solution**: Implemented transactional batch submission for nominees where the entire nominee array is validated atomically inside a single `@Transactional` boundary.

---

### Module 2: KYC & Compliance Verification (`kyc-service`)

#### 2. The Presentation Pitch:
> *"KYC Service is the regulatory firewall of the PAS. Under IRDAI and Anti-Money Laundering (AML) mandates, no quotation or underwriting can occur without verified identity. It validates government IDs, connects with Central KYC (CKYC) registries, and logs an immutable audit trail."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `customer_kyc` (`INS006`), `kyc_document` (`INS007`), `ckyc_details` (`INS008`), `kyc_verification_log` (`INS009`).
* **Statuses**: `PENDING`, `VERIFIED`, `REJECTED`, `RE_SUBMITTED`.

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - Uploading documents, retrieving document metadata, updating verification status.
* **Beyond CRUD (The Real Engineering)**:
  1. **Algorithmic Checksum Validation**:
     - **PAN Validation**: Strict regex matching `[A-Z]{5}[0-9]{4}[A-Z]{1}` where character 4 verifies entity type (`P` for individual).
     - **Aadhaar Verification**: Implemented **Verhoeff checksum algorithm** on the 12-digit string to reject fake numbers before initiating network requests.
  2. **CKYC Registry Querying**: Asynchronously checks the central government CKYC database using customer PAN/Aadhaar. If found, auto-populates demographic records to minimize manual uploads.
  3. **Hard Regulatory Circuit Breaker**: Emits an event or exposes an API that `quote-service` and `underwriting-service` check. If KYC is not `VERIFIED`, any downstream policy progression is hard-blocked.

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge**: Uploaded document images (PDF/PNG) can be maliciously large or contain invalid file extensions.
  - **Solution**: Implemented a multipart file filter validating MIME types (magic bytes inspection, not just filename extension) and limiting file size to 5MB.

---

### Module 3: Insurance Product Management (`product-service`)

#### 1. The Presentation Pitch:
> *"Product Service is the catalog master that defines the insurance policies we sell. It configures product categories, eligibility criteria, sum insured limits, allowable riders, commission percentages, and premium frequencies."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `product_category` (`INS011`), `insurance_product` (`INS012`), `coverage_master` (`INS013`), `rider_master` (`INS014`), `benefit_master` (`INS015`), `premium_frequency` (`INS016`), `product_commission` (`INS017`).

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - Admin endpoints to create product categories, add coverage types, and configure riders.
* **Beyond CRUD (The Real Engineering)**:
  1. **Immutable Product Versioning**: An insurance product cannot simply be modified via `PUT` if customers already hold policies under that product. Any rate change creates a new product version (`v2`), preserving `v1` terms for active policy contracts.
  2. **Dynamic Eligibility Matrix**: Evaluates complex constraints:
     $$\text{Min Age} \le \text{Applicant Age} \le \text{Max Age}$$
     $$\text{Min Sum Insured} \le \text{Coverage} \le \text{Max Sum Insured}$$
  3. **Rider Mutual Exclusions**: Validates which riders can be legally attached to specific product types (e.g., Accidental Disability cannot attach to Property insurance).

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge**: Preventing administrators from deleting products that have active policies attached.
  - **Solution**: Replaced hard `DELETE` with a soft-deactivation pattern (`status = INACTIVE`) and a referential integrity check across policy databases.

---

### Module 4: Quote Generation & Premium Calculation (`quote-service`)

#### 1. The Presentation Pitch:
> *"Quote Service is the actuarial computation engine. It calculates the financial premium using multi-variable actuarial formulas, applies risk loading, bundles optional riders, applies commercial discounts, computes 18% GST, and issues a 30-day legally binding quotation."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `quote` (`INS018`), `premium_calculation` (`INS019`), `risk_assessment` (`INS020`), `quote_rider` (`INS021`), `discount_master` (`INS022`), `quote_history` (`INS023`).

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - `POST /quotes`: Stores quote inputs.
  - `GET /quotes/{id}`: Displays quotation breakdown to customer.
* **Beyond CRUD (The Real Engineering)**:
  1. **Actuarial Calculation Pipeline**:
     $$\text{Base Premium} = \frac{\text{Sum Insured} \times \text{Product Rate}}{1000}$$
     $$\text{Risk Loading} = \text{Base Premium} \times \text{Risk Loading \%}$$
     $$\text{Rider Premium} = \sum \text{Selected Riders}$$
     $$\text{Taxable Premium} = (\text{Base} + \text{Loading} + \text{Riders}) - \text{Discount}$$
     $$\text{Final Premium} = \text{Taxable Premium} + (\text{Taxable Premium} \times 18\% \text{ GST})$$
  2. **Dynamic Risk Scoring**: Evaluates customer age, occupation category (Low risk Software Engineer vs High risk Firefighter/Miner), and lifestyle habits (smoking/alcohol) to assign a risk score ($0-100$).
  3. **30-Day Legal Price Lock**: Generates `expiry_date = quote_date + 30 days`. Quotes older than 30 days are automatically expired and cannot be converted into policies.

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge**: Floating-point rounding errors causing ₹0.01 discrepancies in financial balance sheets.
  - **Solution**: Used Java's `BigDecimal` with explicit `RoundingMode.HALF_UP` for all intermediate and tax calculations.

---

### Module 5: Underwriting & Risk Decision Engine (`underwriting-service`)

#### 1. The Presentation Pitch:
> *"Underwriting Service is the risk gatekeeper. It evaluates the financial and medical feasibility of insuring an applicant. It features a Straight-Through Processing (STP) rule engine for instant auto-approvals, while routing complex medical profiles to human underwriters for loading or exclusion decisions."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `underwriting_case` (`INS024`), `medical_underwriting` (`INS025`), `financial_underwriting` (`INS026`), `risk_score` (`INS027`), `underwriting_decision` (`INS028`), `underwriting_audit_log` (`INS029`).
* **Decision Types**: `APPROVED`, `APPROVED_WITH_LOADING`, `APPROVED_WITH_EXCLUSIONS`, `REJECTED`, `MEDICAL_PENDING`.

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - Captures medical details (height, weight, history) and financial income slips.
* **Beyond CRUD (The Real Engineering)**:
  1. **Automated STP Rule Engine**:
     - If Risk Score $\le 30$, Age $< 45$, and Sum Insured $< ₹25,00,000$, auto-approves the proposal in milliseconds without human touch.
  2. **Medical Underwriting Math**:
     - Calculates BMI: $\text{BMI} = \frac{\text{Weight (kg)}}{(\text{Height (m)})^2}$.
     - If $\text{BMI} > 32$ or cardiovascular history is present, automatically applies a $10\%-25\%$ premium loading.
  3. **Human Life Value (HLV) Financial Guard**:
     - Validates that the requested cover does not exceed financial capability: $\text{Max Cover} \le 20 \times \text{Annual Income}$.

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge**: Underwriters modifying decisions without traceability.
  - **Solution**: Built an immutable `underwriting_audit_log` recording the underwriter's employee ID, timestamp, previous status, and mandatory rejection/loading rationale.

---

### Module 6: Policy Issuance Module (`policy-service`)

#### 1. The Presentation Pitch:
> *"Policy Issuance Service converts approved underwriting proposals into legally binding contracts. It generates enterprise policy numbers, coordinates first premium collection with Premium Service, and compiles digital PDF policy contracts stamped with digital signatures."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `policy` (`INS030`), `policy_coverage` (`INS031`), `policy_rider` (`INS032`), `policy_document` (`INS033`), `policy_endorsement` (`INS034`), `policy_history` (`INS035`).

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - `GET /policies/{id}`: Fetches policy terms, dates, and status.
* **Beyond CRUD (The Real Engineering)**:
  1. **Structured Policy Number Generator**:
     - Generates audit-compliant identifiers: `{PRODUCT}-{STATE}-{YEAR}-{SEQUENCE}` (e.g., `HLT-MH-2026-000100245`).
  2. **Contract State Machine**:
     - Policy starts as `DRAFT`.
     - Moves to `PENDING_PAYMENT`.
     - **Transitions to `ACTIVE` only upon receiving payment confirmation from Premium Service**.
  3. **Digital PDF Document Generation**:
     - Merges coverage limits, terms, exclusions, nominee details, and payment receipts into a single downloadable legal PDF contract.

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge**: Distributing policy status changes to other microservices reliably.
  - **Solution**: OpenFeign endpoints paired with Feign fallbacks to ensure graceful degradation if downstream services experience network latency.

---

### Module 7: Premium Schedule & Premium Payment (`premium-service`) — YOUR STAR MODULE!

#### 1. The Presentation Pitch:
> *"Premium Service is the financial collection engine. It automatically breaks down policy premiums into installments based on frequency, manages 15-day and 30-day grace periods, processes payments with strict duplicate-prevention, issues digital tax receipts, handles refunds, and operates 3 autonomous background schedulers."*

#### 2. Key Things to Know Before Presenting:
* **Tables**:
  - `premium_schedules` (`INS036`): Installments, due dates, late fees, status, grace periods.
  - `premium_payments` (`INS037`): Financial transactions, payment modes, transaction references.
  - `payment_receipts` (`INS038`): Unique receipts (`REC-2026-XXXXXX`) and paths.
  - `premium_reminders` (`INS039`): Outbound reminder logs across 5 stages.
  - `premium_refunds` (`INS040`): Refund requests and dual-authorization approvals.
* **Schedulers**:
  1. `PremiumOverdueScheduler` (Midnight, daily)
  2. `PremiumReminderScheduler` (08:00 AM, daily)
  3. `PaymentReconciliationScheduler` (Every 15 min)

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **CRUD Portion**:
  - Storing payment transactions and schedules.
* **Beyond CRUD (The Real Engineering)**:
  1. **Penny-Rounding Frequency Splitting**:
     - Divides premium evenly across installments and adjusts rounding pennies on the final installment so $\sum \text{Installments} == \text{Final Premium}$.
  2. **Transaction Idempotency**:
     - Validates `existsByTransactionReference(txnRef)` to guarantee a customer is never double-charged if they click "Pay" multiple times.
  3. **3 Autonomous Schedulers**:
     - Evaluates grace periods and calculates dynamic late fees daily.
     - Dispatches multi-stage notifications at T-15, T-7, Due Date, T+3, and T+7.
     - Cleans up orphaned `INITIATED` payments.
  4. **Dual-Authorization Refund Workflow**:
     - Refunds must be initiated and subsequently approved by a Finance Officer (`approvedBy`) before funds are released.

#### 4. Challenges Faced & Engineering Solutions:
* **Challenge 1 (Compile Error)**: `PolicyClientFallback` constructor had parameter mismatch with `PolicyDto`.
  - **Solution**: Synchronized all 8 parameters in `PolicyClientFallback`.
* **Challenge 2 (Missing Schedulers & Feign)**: Background jobs and Feign proxies were inactive.
  - **Solution**: Annotated `PremiumServiceApplication` with `@EnableScheduling` and `@EnableFeignClients`.
* **Challenge 3 (Stalled Stubs)**: Controller endpoints were returning `null`.
  - **Solution**: Implemented end-to-end service layers, repositories, and DTO mappers.

---

### Module 8: Policy Servicing & Endorsements

#### 1. The Presentation Pitch:
> *"Policy Servicing manages all post-issuance customer requests throughout the 10-30 year lifecycle of a policy. It processes financial endorsements (coverage increases), non-financial endorsements (address/nominee updates), policy cancellations, and annual renewals."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `service_request` (`INS041`), `policy_endorsement` (`INS042`), `nominee_details` (`INS043`), `policy_renewal` (`INS044`), `policy_cancellation` (`INS045`), `service_request_history` (`INS046`).

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **Beyond CRUD (The Real Engineering)**:
  1. **Endorsement Splitting**: Differentiates between non-risk changes (instant OTP update) and risk-bearing changes (coverage increase triggers underwriting and premium recalculation).
  2. **Free-Look Cancellation & Refund Trigger**: If cancelled within 15 days of policy issuance, calls `premium-service: POST /api/v1/refunds` to return premiums.

---

### Module 9: Claims Management & Settlement (`claims-service`)

#### 1. The Presentation Pitch:
> *"Claims Management is the moment of truth for the insurance company. It handles claim registration, surveyor/loss-assessor assignment, fraud investigation, depreciation and deductible deductions, and automated payment settlement."*

#### 2. Key Things to Know Before Presenting:
* **Tables**: `claim_master` (`INS047`), `claim_documents` (`INS048`), `survey_assignment` (`INS049`), `investigation` (`INS050`), `claim_assessment` (`INS051`), `claim_settlement` (`INS052`), `claim_history` (`INS053`).

#### 3. How We Perform CRUD vs. What We Do Beyond CRUD:
* **Beyond CRUD (The Real Engineering)**:
  1. **Claim Settlement Mathematical Formula**:
     $$\text{Settlement} = \text{Claim Amount} - \text{Deductible} - \text{Depreciation} - \text{Non-Payable Expenses}$$
  2. **Inter-Service Premium Guard**: Before approving any claim, calls `premium-service: GET /api/v1/premium-schedules/{policyId}`. If an installment is `OVERDUE`, the claim is suspended until premium arrears are settled.
  3. **Surveyor Allocation Rule**: Motor or Property claims exceeding ₹50,000 automatically mandate an independent licensed surveyor inspection before claims approval.

---

### Module 10: Reports, Security & Cloud Infrastructure

#### 1. Key Components:
* **API Gateway (`api-gateway`)**: Central routing, rate limiting, and SSL termination.
* **Service Registry (`service-registry`)**: Netflix Eureka / Consul for client-side discovery.
* **JWT & Spring Security**: Role-Based Access Control (`CUSTOMER`, `AGENT`, `UNDERWRITER`, `FINANCE_OFFICER`, `ADMIN`).
* **Reports Service**: Aggregates collection efficiency and loss ratios for executive dashboards.

---

## 3. High-Impact Presentation Q&A (How to Ace Difficult Questions)

### Q1: "Why did you separate Premium Service from Policy Issuance Service?"
> **Your Answer**: *"In insurance, policy issuance happens once at contract inception, whereas premium payments and installment schedules run continuously for up to 30 years with high concurrent traffic spikes during due dates. Decoupling them allows us to independently scale the premium service and its background schedulers without placing load on core policy issuance databases."*

### Q2: "What happens if a customer clicks 'Pay Premium' twice simultaneously?"
> **Your Answer**: *"We designed the payment flow with strict idempotency. Every payment request carries a unique `transactionReference`. In `PremiumPaymentServiceImpl`, we execute `paymentRepository.existsByTransactionReference(txnRef)` and enforce a unique constraint at the database layer. If a second thread arrives with the same reference, it is immediately rejected with a `DuplicatePaymentException` (HTTP 409 Conflict), preventing double deduction."*

### Q3: "What happens if your scheduled job crashes halfway through processing 10,000 overdue installments?"
> **Your Answer**: *"The job is idempotent. It selects records where `dueDate + gracePeriod < today` AND `status IN ('PENDING', 'PARTIAL_PAID')`. Once an installment is updated to `OVERDUE`, it will not be re-processed on subsequent runs. If a crash occurs, re-running the job immediately picks up where it left off without duplicating penalties."*

### Q4: "Why use OpenFeign instead of RestTemplate?"
> **Your Answer**: *"OpenFeign provides declarative, contract-first HTTP client interfaces with built-in Spring Cloud LoadBalancer integration and out-of-the-box fallback support. It eliminates repetitive URL concatenation, HTTP headers boilerplate, and error-prone JSON mapping."*

---

## 4. Presentation Delivery Roadmap (Your 10-Minute Script)

```
[00:00 - 01:30] Introduction & Architecture:
               • Mention microservices decomposition and regulatory context (IRDAI).
               • Show the architecture flow: Customer ➔ KYC ➔ Quote ➔ Underwriting ➔ Policy.

[01:30 - 03:30] Upstream Domain Services:
               • Highlight deduplication in Customer, Verhoeff checksum in KYC.
               • Explain actuarial formulas in Quote and STP rule engine in Underwriting.

[03:30 - 07:00] Deep Dive on Premium Service (Your Core Module):
               • Explain installment schedule generation and rounding penny adjustment.
               • Walk through payment idempotency, receipt generation, and refund dual-approval.
               • Explain the 3 Schedulers: Overdue (Grace/Late fees), Reminder (T-15 to T+7), Reconciliation.

[07:00 - 08:30] Cross-Service Integration:
               • Show how Policy Issuance calls Premium Service to generate schedules.
               • Show how Claims Service verifies premium standing before claim approval.
               • Explain OpenFeign and client-side load balancing.

[08:30 - 10:00] Conclusion & Live Code / Demo / Q&A.
```
