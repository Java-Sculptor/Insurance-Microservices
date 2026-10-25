# Insurance PAS Ecosystem: Domain Modules, Advanced Business Logic, and Premium Service Integration Guide

> **Target Audience**: Backend Developers, Microservice Engineers, Technical Architects  
> **System Context**: Insurance Policy Administration System (PAS)  
> **Complementary Guide**: See [PREMIUM_SERVICE_ARCHITECTURE_AND_SCHEDULER_GUIDE.md](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/PREMIUM_SERVICE_ARCHITECTURE_AND_SCHEDULER_GUIDE.md) for internal service deep-dive.

---

## Table of Contents
1. [End-to-End Insurance Business Lifecycle](#1-end-to-end-insurance-business-lifecycle)
2. [Ecosystem Modules: Deep Dive Beyond Basic CRUD](#2-ecosystem-modules-deep-dive-beyond-basic-crud)
   - 2.1 [Customer Registration & Profile Module (`customer-service`)](#21-customer-registration--profile-module)
   - 2.2 [KYC & Compliance Verification Module (`kyc-service`)](#22-kyc--compliance-verification-module)
   - 2.3 [Insurance Product Management Module (`product-service`)](#23-insurance-product-management-module)
   - 2.4 [Quote Generation & Premium Calculation Module (`quote-service`)](#24-quote-generation--premium-calculation-module)
   - 2.5 [Underwriting & Risk Decision Engine (`underwriting-service`)](#25-underwriting--risk-decision-engine)
   - 2.6 [Policy Issuance Module (`policy-service`)](#26-policy-issuance-module)
   - 2.7 [Policy Servicing Module (Endorsements & Renewals)](#27-policy-servicing-module)
   - 2.8 [Claims Management Module (`claims-service`)](#28-claims-management-module)
   - 2.9 [Reports, MIS & Analytics Module](#29-reports-mis--analytics-module)
3. [When and Where Premium Service Enters the Picture](#3-when-and-where-premium-service-enters-the-picture)
4. [Endpoint-by-Endpoint Deep Dive: Premium Service](#4-endpoint-by-endpoint-deep-dive-premium-service)
   - 4.1 [Schedule Management Endpoints](#41-schedule-management-endpoints)
   - 4.2 [Payment Collection & Processing Endpoints](#42-payment-collection--processing-endpoints)
   - 4.3 [Receipt Management Endpoints](#43-receipt-management-endpoints)
   - 4.4 [Refund Lifecycle Endpoints](#44-refund-lifecycle-endpoints)
5. [Cross-Module Choreography: How Services Consume Premium Service](#5-cross-module-choreography-how-services-consume-premium-service)
   - 5.1 [Policy Issuance ➔ Premium Schedule Creation](#51-policy-issuance--premium-schedule-creation)
   - 5.2 [Payment Success ➔ Policy Activation & Receipt Dispatch](#52-payment-success--policy-activation--receipt-dispatch)
   - 5.3 [Claims Management ➔ Premium Eligibility Verification](#53-claims-management--premium-eligibility-verification)
   - 5.4 [Policy Cancellation ➔ Premium Refund Trigger](#54-policy-cancellation--premium-refund-trigger)
   - 5.5 [Executive Dashboard ➔ Financial Aggregation](#55-executive-dashboard--financial-aggregation)
6. [Summary Reference Architecture](#6-summary-reference-architecture)

---

## 1. End-to-End Insurance Business Lifecycle

In enterprise insurance software, an insurance policy is a legally binding contract. A policy cannot simply be inserted into a database; it must pass through strict regulatory gates, underwriting risk assessments, and financial validations.

```
┌──────────────┐     ┌──────────────┐     ┌──────────────┐     ┌──────────────┐
│ 1. Customer  │ ──> │    2. KYC    │ ──> │  3. Product  │ ──> │   4. Quote   │
│ Registration │     │ Verification │     │  Selection   │     │  Generation  │
└──────────────┘     └──────────────┘     └──────────────┘     └──────┬───────┘
                                                                      │
┌──────────────┐     ┌──────────────┐     ┌──────────────┐            │
│ 7. Policy    │ <── │ 6. Policy    │ <── │ 5. Under-    │ <──────────┘
│  Active      │     │  Issuance    │     │   writing    │
└──────┬───────┘     └──────────────┘     └──────────────┘
       │
       ▼ (During Policy Tenure)
┌────────────────────────────────────────────────────────┐
│  ★ PREMIUM SERVICE (Schedule, Schedulers, Payments)    │
└──────┬──────────────────────────────────┬──────────────┘
       │                                  │
       ▼                                  ▼
┌──────────────┐                   ┌──────────────┐
│  8. Policy   │                   │  9. Claims   │
│  Servicing   │                   │  Management  │
│  & Renewals  │                   │ & Settlement │
└──────────────┘                   └──────────────┘
```

---

## 2. Ecosystem Modules: Deep Dive Beyond Basic CRUD

Most beginners mistake enterprise microservices for basic CRUD (Create, Read, Update, Delete) wrappers around tables. In a real-world enterprise Insurance Policy Administration System (PAS), each service solves complex algorithmic, compliance, and distributed workflow problems.

---

### 2.1 Customer Registration & Profile Module (`customer-service`)

#### Why We Need It:
Centralizes policyholder identity. In insurance, a single individual might hold multiple policies (e.g., Term Life, Family Health, Car Insurance). Creating duplicate customer records leads to fragmented risk tracking, duplicate AML checks, and lost customer history.

#### What It Does:
* Onboards customers, captures personal data, addresses (Permanent & Communication), and occupation profile.
* Manages nominee details (nominee relationship, date of birth, share percentage summing to 100%).

#### Advanced Logic (Beyond CRUD):
1. **Deduplication Engine**:
   - Before saving, checks fuzzy matching on `First Name + Last Name + DOB + Mobile + Email`.
   - Prevents an agent from creating a new customer profile if the customer already exists in another branch.
2. **Nominee Share Validation Engine**:
   - Validates that the sum of all nominee share percentages equals exactly `100.00%`.
   - If a nominee is a minor ($< 18$ years), strictly enforces the mandatory capture of guardian name and relationship.
3. **Pincode & Address Standardization**:
   - Validates pincodes against a Postal Master database to automatically populate city, district, and state, avoiding typographical errors.

---

### 2.2 KYC & Compliance Verification Module (`kyc-service`)

#### Why We Need It:
Required by insurance regulators (like **IRDAI**) and Anti-Money Laundering (**AML**) laws. Selling a policy to an unverified individual or money launderer exposes the insurer to regulatory penalties and license revocation.

#### What It Does:
* Collects and verifies government ID proofs (Aadhaar, PAN, Passport, Voter ID, Driving License).
* Integrates with Central KYC (**CKYC**) registries.

#### Advanced Logic (Beyond CRUD):
1. **Algorithmic Document Number Validation**:
   - Validates **PAN** using regular expression and checksum algorithm: `[A-Z]{5}[0-9]{4}[A-Z]{1}` (4th character represents status: `P` for Individual, `C` for Company).
   - Validates **Aadhaar** using the **Verhoeff checksum algorithm** to verify authenticity before initiating external OTP verification.
2. **CKYC Registry Integration**:
   - Submits CKYC search query to central government registry.
   - If a record exists, automatically downloads the verified demographic dataset, populating customer details and reducing customer drop-off.
3. **Hard Regulatory Gate**:
   - Implements a circuit breaker rule: **Quote Generation and Underwriting are strictly blocked** until `kyc_status == 'VERIFIED'`.

---

### 2.3 Insurance Product Management Module (`product-service`)

#### Why We Need It:
Insurance companies do not sell fixed products. They sell dynamic products with configurable rules, age limits, waiting periods, riders, deductibles, and premium frequencies.

#### What It Does:
* Maintains product categories (Life, Health, Motor, Travel, Property).
* Manages product master configurations, eligibility rules, and commission rates.

#### Advanced Logic (Beyond CRUD):
1. **Product Versioning**:
   - When an insurance product updates its terms or pricing, existing issued policies must retain their original signed conditions. The product service manages versioned product states (`v1`, `v2`) so existing contracts are never corrupted.
2. **Dynamic Rule & Matrix Engine**:
   - Configures multidimensional eligibility tables:
     $$\text{Min Age} \le \text{Customer Age} \le \text{Max Age}$$
     $$\text{Min Sum Insured} \le \text{Selected Cover} \le \text{Max Sum Insured}$$
3. **Rider Compatibility & Mutual Exclusions**:
   - Enforces business rules on which optional riders can be bundled (e.g., Critical Illness cannot be attached to a Motor policy; Accidental Death cannot exceed $100\%$ of base sum insured).

---

### 2.4 Quote Generation & Premium Calculation Module (`quote-service`)

#### Why We Need It:
Before a policy is issued, the customer needs a binding quotation. Calculating premiums involves actuarial mathematics taking into account age, smoking habits, hazardous occupations, medical conditions, riders, discounts, and tax (GST).

#### What It Does:
* Generates quotes, calculates premiums, applies discounts, evaluates initial risk tier, and manages 30-day quote expiration.

#### Advanced Logic (Beyond CRUD):
1. **Actuarial Premium Calculation Engine**:
   ```
   Base Premium = (Sum Insured × Product Rate) / 1000
   Risk Loading = Base Premium × Risk Loading %
   Rider Premium = Sum(Selected Optional Riders)
   Gross Premium = Base Premium + Risk Loading + Rider Premium
   Net Taxable Premium = Gross Premium - (Gross Premium × Discount %)
   GST = Net Taxable Premium × 18%
   Final Premium = Net Taxable Premium + GST
   ```
2. **Risk Scoring Engine**:
   - Calculates customer risk score based on parameters:
     - Age ($0-20$ pts)
     - Medical History ($0-30$ pts)
     - Occupation Risk category ($0-20$ pts)
     - Lifestyle/Smoking ($0-20$ pts)
     - Financial Stability ($0-10$ pts)
   - Score $0-30$ = Low Risk (auto-approvable)  
   - Score $31-70$ = Medium Risk (standard loading)  
   - Score $71-100$ = High Risk (triggers mandatory underwriter review)
3. **Quote Expiration & Price Lock**:
   - Quotations are legally binding for exactly 30 days. The service enforces quote expiry timestamps, preventing customers from paying an outdated quote after rate increases.

---

### 2.5 Underwriting & Risk Decision Engine (`underwriting-service`)

#### Why We Need It:
Underwriting is the financial risk evaluation engine of the insurance company. An insurer cannot accept every quote blindly; underwriters determine if the company should accept the risk, charge an additional premium (loading), add exclusions, or reject the application.

#### What It Does:
* Evaluates medical history, calculates BMI, checks financial adequacy (income vs. cover), assigns cases to human underwriters, and records legal decisions.

#### Advanced Logic (Beyond CRUD):
1. **Straight-Through Processing (STP) Rule Engine**:
   - If customer risk score is Low ($\le 30$), sum insured is below threshold ($< ₹25,00,000$), and age is $< 45$, the system executes **Automatic Approval** without human underwriter intervention.
2. **Medical Underwriting Algorithms**:
   - Auto-computes **BMI**:
     $$\text{BMI} = \frac{\text{Weight (kg)}}{(\text{Height (m)})^2}$$
   - If $\text{BMI} \ge 32$ (Obesity Class II) or history of Diabetes/Hypertension is present, auto-applies a **10% to 25% Premium Loading**.
3. **Financial Underwriting Ratio**:
   - Enforces Human Life Value (HLV) limits: Maximum allowed sum insured cannot exceed $20 \times \text{Annual Income}$ for applicants under 35, or $10 \times \text{Annual Income}$ for applicants over 50.

---

### 2.6 Policy Issuance Module (`policy-service`)

#### Why We Need It:
Converts an approved underwriting proposal into a legally recognized contract. It creates the official policy record, binds coverage, and generates digital legal contracts.

#### What It Does:
* Generates unique policy numbers, coordinates first premium collection, activates policies, generates PDF schedules, and maps coverage.

#### Advanced Logic (Beyond CRUD):
1. **Enterprise Policy Number Generation**:
   - Generates non-sequential, audit-safe policy numbers formatted by product and region:
     `{PRODUCT_CODE}-{STATE_CODE}-{YEAR}-{SEQUENCE}` (e.g., `HLT-MH-2026-00014523`).
2. **Digital Contract (PDF) Compilation**:
   - Ingests customer details, nominee details, coverage limits, terms, exclusions, and stamps the company digital certificate and QR code onto a formal policy document.
3. **State Machine Transition**:
   - Transitions policy: `DRAFT` ➔ `PENDING_PAYMENT` ➔ `ACTIVE`.
   - **Crucial Link**: A policy cannot become `ACTIVE` until `premium-service` confirms that the initial premium has been paid!

---

### 2.7 Policy Servicing Module (Endorsements & Renewals)

#### Why We Need It:
An insurance policy lasts for 1 to 30 years. During this time, customers change addresses, marry, change nominees, request higher coverage, or renew expiring policies.

#### What It Does:
* Processes policy modifications (**Endorsements**), manages annual renewals, and handles cancellation requests.

#### Advanced Logic (Beyond CRUD):
1. **Financial vs. Non-Financial Endorsement Splitting**:
   - *Non-Financial* (Address change, email update): Auto-approved via OTP.
   - *Financial* (Sum insured increase, adding critical illness rider): Triggers underwriting re-assessment and sends an adjusted premium schedule to `premium-service`.
2. **Immutable Audit Versioning**:
   - Every endorsement generates a before/after snapshot (`previous_value` vs `updated_value`) to preserve historical legal evidence for future claims.

---

### 2.8 Claims Management Module (`claims-service`)

#### Why We Need It:
The ultimate promise of an insurance company is to pay valid claims when an incident occurs (hospitalization, accident, theft, death).

#### What It Does:
* Registers claims, assigns independent surveyors, evaluates losses, and coordinates settlement payments.

#### Advanced Logic (Beyond CRUD):
1. **Claim Settlement Calculation Engine**:
   $$\text{Net Settlement} = \text{Claim Amount} - \text{Deductible} - \text{Depreciation} - \text{Non-Payable Items}$$
2. **Fraud Detection Triggers**:
   - Flags suspicious claims: claim registered within 30 days of policy issuance (early claim), multiple claims in 12 months, or loss occurring during policy lapse/grace periods.
3. **Premium Continuity Verification**:
   - **Crucial Link**: Before any claim is registered or settled, `claims-service` calls `premium-service` to verify that all premium installments are up to date!

---

### 2.9 Reports, MIS & Analytics Module

#### Why We Need It:
Provides executives, underwriters, and compliance regulators with real-time visibility into collection ratios, loss ratios, and business performance.

#### Advanced Logic (Beyond CRUD):
* Computes core insurance metrics:
  $$\text{Loss Ratio} = \left(\frac{\text{Total Claims Paid}}{\text{Total Premium Collected}}\right) \times 100$$
  $$\text{Collection Efficiency} = \left(\frac{\text{Actual Premium Collected}}{\text{Scheduled Premium Due}}\right) \times 100$$

---

## 3. When and Where Premium Service Enters the Picture

`premium-service` is the financial heartbeat of the Insurance PAS. Here is its exact entry point in the customer journey:

```
                  CHRONOLOGICAL TIME HORIZON
 Day 0           Day 0             Day 0             Day 1 .. 365+
┌──────────────┐┌────────────────┐┌────────────────┐┌───────────────────────┐
│ Underwriting ││ Policy Issued  ││ First Premium  ││ Lifecycle Maintenance │
│ Approved     ││ (Draft Status) ││ Payment        ││ (Schedulers Active)   │
└──────┬───────┘└───────┬────────┘└───────┬────────┘└──────────┬────────────┘
       │                │                 │                    │
       ▼                ▼                 ▼                    ▼
   Underwriter      Policy-Service    Customer Pays     • Overdue Scheduler
   gives green      calls:            POST /payments    • Reminder Scheduler
   light.           POST /premium-    ➔ Receipt Issued  • Reconcil. Scheduler
                    schedules         ➔ Policy ACTIVE   • Claims Validation
```

1. **Trigger Moment 1: Policy Issuance**:
   - As soon as underwriting approves a proposal, `policy-issuance-service` generates a draft policy.
   - It immediately calls `premium-service`: `POST /api/v1/premium-schedules`.
   - `premium-service` creates all future installment records with due dates and grace periods.
2. **Trigger Moment 2: First Premium Collection**:
   - The customer pays Installment #1 via `POST /api/v1/payments`.
   - `premium-service` validates the transaction, generates a receipt, and notifies `policy-service`.
   - `policy-service` switches the policy status from `PENDING_PAYMENT` to `ACTIVE`.
3. **Trigger Moment 3: Policy Maintenance (Background Schedulers)**:
   - Throughout the policy term, `premium-service` schedulers run daily:
     - Sending reminders at T-15, T-7, Due Date, T+3, T+7.
     - Applying late fee penalties after grace periods expire.
     - Cleaning up orphaned payments every 15 minutes.
4. **Trigger Moment 4: Claims & Cancellations**:
   - If a claim is filed, `claims-service` calls `premium-service` to check premium standing.
   - If a policy is cancelled, `policy-service` calls `premium-service` to execute a refund.

---

## 4. Endpoint-by-Endpoint Deep Dive: Premium Service

Every single endpoint built in `premium-service` is documented below with its business rationale, internal mechanics, and consumer contract.

---

### 4.1 Schedule Management Endpoints

#### 1. `POST /api/v1/premium-schedules`
* **Purpose**: Generates installment schedules for a policy based on term, frequency, and total premium.
* **Why We Need It**: When a policy is sold, the customer selects a payment frequency (Monthly, Quarterly, Half-Yearly, Yearly, Single). The system must break the total cost into predictable, legally binding installment milestones.
* **Under the Hood**:
  1. Inspects `premiumFrequency` (`MONTHLY`, `QUARTERLY`, `HALF_YEARLY`, `YEARLY`, `SINGLE_PREMIUM`).
  2. Determines installment count and grace period (15 days for monthly, 30 days for others).
  3. Divides `finalPremium` by installment count using `RoundingMode.HALF_UP`.
  4. Fixes any rounding discrepancies on the final installment.
  5. Sets Installment #1 due date to `startDate` and subsequent installments spaced by month intervals.
  6. Saves all records to `premium_schedules` table with status `PENDING`.
* **Primary Consumer**: `policy-issuance-service` (via Feign Client or REST).
* **Sample Request Payload**:
  ```json
  {
    "policyId": 100245,
    "policyTermMonths": 12,
    "premiumFrequency": "QUARTERLY",
    "startDate": "2026-10-20",
    "finalPremium": 12000.00
  }
  ```
* **Sample Response Payload**:
  ```json
  {
    "success": true,
    "message": "Premium schedules generated successfully",
    "data": [
      {
        "scheduleId": 1,
        "policyId": 100245,
        "installmentNo": 1,
        "dueDate": "2026-10-20",
        "premiumAmount": 3000.00,
        "lateFee": 0.00,
        "gracePeriodDays": 30,
        "totalPayable": 3000.00,
        "status": "PENDING"
      },
      {
        "scheduleId": 2,
        "policyId": 100245,
        "installmentNo": 2,
        "dueDate": "2027-01-20",
        "premiumAmount": 3000.00,
        "lateFee": 0.00,
        "gracePeriodDays": 30,
        "totalPayable": 3000.00,
        "status": "PENDING"
      }
    ]
  }
  ```

---

#### 2. `GET /api/v1/premium-schedules/{policyId}`
* **Purpose**: Retrieves all installment schedules for a given policy ID ordered by installment number.
* **Why We Need It**: Customer portals, mobile apps, and agent dashboards need to display upcoming payment dues, paid dates, and overdue penalties.
* **Under the Hood**: Queries `scheduleRepository.findByPolicyIdOrderByInstallmentNoAsc(policyId)` and maps entities to `ScheduleResponseDto`.
* **Primary Consumer**: Customer Portal, Agent Portal, Admin UI.

---

#### 3. `PUT /api/v1/premium-schedules/{scheduleId}`
* **Purpose**: Updates an installment schedule's status or manual late fee adjustment.
* **Why We Need It**: If a customer receives a special managerial penalty waiver, or an installment must be cancelled due to policy endorsement, the finance officer updates the record.
* **Under the Hood**: Fetches `PremiumSchedule` by ID, updates `status` (e.g., `CANCELLED`, `OVERDUE`) and/or `lateFee`, saves to database, and returns updated DTO.
* **Primary Consumer**: Branch Manager / Finance Officer Portal.

---

#### 4. `POST /api/v1/premium-schedules/trigger-overdue-assessment`
* **Purpose**: Manually triggers the overdue assessment and late fee engine on demand.
* **Why We Need It**: Facilitates QA testing, emergency administrative runs, or ad-hoc runs without waiting for the midnight cron job.
* **Under the Hood**: Invokes `premiumScheduleService.assessOverdueAndCalculateLateFees()` and returns the total count of modified installments.
* **Primary Consumer**: DevOps / System Administrator / Integration Test Suites.

---

### 4.2 Payment Collection & Processing Endpoints

#### 5. `POST /api/v1/payments`
* **Purpose**: Processes a premium payment against an installment schedule.
* **Why We Need It**: Core transaction endpoint. Accepts customer funds, ensures no double-charging occurs, updates installment status, and triggers receipt creation.
* **Under the Hood**:
  1. Checks idempotency: if `transactionReference` already exists, throws `DuplicatePaymentException`.
  2. Fetches the `PremiumSchedule`; verifies it is not already `PAID`.
  3. Validates policy status using `PolicyClient` (must be `ACTIVE`, `DRAFT`, or `PENDING_PAYMENT`).
  4. Saves `PremiumPayment` record with `SUCCESS` status.
  5. Computes total due (`premiumAmount + lateFee`). If `amountPaid >= totalDue`, marks schedule `PAID`, otherwise `PARTIAL_PAID`.
  6. Generates and stores a unique `PaymentReceipt` (`REC-2026-XXXXXX`).
* **Primary Consumer**: Customer Portal (Checkout), Payment Gateway Webhook, Branch Cashier.
* **Sample Request Payload**:
  ```json
  {
    "scheduleId": 1,
    "policyId": 100245,
    "customerId": 8801,
    "amountPaid": 3000.00,
    "paymentMode": "UPI",
    "transactionReference": "UPI-HDFC-99882211"
  }
  ```

---

#### 6. `GET /api/v1/payments/{paymentId}`
* **Purpose**: Retrieves single payment transaction details by its primary ID.
* **Why We Need It**: Used by customer receipt screens and finance audit tools to inspect transaction references and timestamps.
* **Under the Hood**: Queries `paymentRepository.findById(paymentId)`.
* **Primary Consumer**: Customer Portal, Accounting System.

---

#### 7. `GET /api/v1/payments/history/{policyId}`
* **Purpose**: Returns the chronological list of all payments made for a policy.
* **Why We Need It**: Customers need tax certificates (Section 80D/80C) and proof of premium payments across multi-year policies.
* **Under the Hood**: Queries `paymentRepository.findByPolicyIdOrderByPaymentDateDesc(policyId)`.
* **Primary Consumer**: Customer Mobile App, Tax Statement Generator.

---

#### 8. `POST /api/v1/payments/verify`
* **Purpose**: Verifies and updates payment status from external payment gateway callbacks.
* **Why We Need It**: In asynchronous payment flows (e.g., NetBanking/Razorpay/Stripe), payment is initially `INITIATED`. The gateway later notifies the system via webhook whether the payment succeeded or failed.
* **Under the Hood**:
  1. Finds the transaction by `transactionReference`.
  2. Updates `paymentStatus` to `SUCCESS` or `FAILED`.
  3. If transitioned to `SUCCESS`, marks the linked installment `PAID` and creates the receipt.
* **Primary Consumer**: Payment Gateway Webhook Dispatcher.
* **Sample Request Payload**:
  ```json
  {
    "transactionReference": "UPI-HDFC-99882211",
    "paymentStatus": "SUCCESS",
    "gatewayTransactionId": "gw_pay_998127391"
  }
  ```

---

### 4.3 Receipt Management Endpoints

#### 9. `GET /api/v1/payments/{paymentId}/receipt`
* **Purpose**: Retrieves digital receipt details for a payment.
* **Why We Need It**: Policyholders require formal premium receipts as legal proof of coverage and for income tax deductions.
* **Under the Hood**: Finds the `PaymentReceipt` by `paymentId`. If not found, generates one on-the-fly and returns full DTO including customer and policy numbers.
* **Primary Consumer**: Customer Portal (Download Receipt button).

---

#### 10. `POST /api/v1/payments/{paymentId}/receipt/regenerate`
* **Purpose**: Forces re-issuance of receipt metadata or PDF path.
* **Why We Need It**: Used when customer details change or if the original receipt file became inaccessible in document storage.
* **Under the Hood**: Re-indexes the receipt metadata, timestamps the generation event, and updates `payment_receipts`.
* **Primary Consumer**: Support Representative / Admin Dashboard.

---

### 4.4 Refund Lifecycle Endpoints

#### 11. `POST /api/v1/refunds`
* **Purpose**: Initiates a premium refund request.
* **Why We Need It**: If a policyholder cancels within the 15-day "Free Look" period, or underwriting cancels a policy post-payment, unutilized premiums must be refunded by law.
* **Under the Hood**:
  1. Validates that the payment exists and was `SUCCESS`.
  2. Ensures `refundAmount <= payment.amountPaid`.
  3. Creates `PremiumRefund` with status `PENDING`.
* **Primary Consumer**: `policy-servicing-service` (on policy cancellation).
* **Sample Request Payload**:
  ```json
  {
    "paymentId": 501,
    "refundAmount": 3000.00,
    "reason": "Policy cancelled during 15-day free-look period"
  }
  ```

---

#### 12. `PUT /api/v1/refunds/{refundId}/approve`
* **Purpose**: Approves or rejects a pending refund request.
* **Why We Need It**: Internal financial control: customer service can initiate refunds, but only a authorized **Finance Officer** or **Administrator** can release company funds.
* **Under the Hood**:
  1. Verifies refund is currently in `PENDING` state.
  2. Updates `refundStatus` to `APPROVED` or `REJECTED`.
  3. Records the `approvedBy` employee ID.
  4. If approved, marks the original payment as `REFUNDED`.
* **Primary Consumer**: Finance Officer Portal.

---

#### 13. `GET /api/v1/refunds/{refundId}` & `GET /api/v1/refunds/policy/{policyId}`
* **Purpose**: Look up specific refund status or all refunds related to a policy.
* **Why We Need It**: Allows tracking whether refund funds have been dispatched back to the customer's bank account.
* **Primary Consumer**: Customer Support, Finance Auditing.

---

## 5. Cross-Module Choreography: How Services Consume Premium Service

This section illustrates how other microservices in the Insurance PAS communicate with `premium-service`.

---

### 5.1 Policy Issuance ➔ Premium Schedule Creation

When underwriting approves an insurance proposal, `policy-issuance-service` generates the policy record and invokes `premium-service`:

```
┌────────────────────────┐                    ┌────────────────────────┐
│ policy-issuance-service│                    │    premium-service     │
└───────────┬────────────┘                    └───────────┬────────────┘
            │                                             │
            │  HTTP POST /api/v1/premium-schedules        │
            │  {policyId: 100245, freq: "QUARTERLY", ...} │
            ├────────────────────────────────────────────>│
            │                                             │
            │                                             │ Calculate installments
            │                                             │ Save PremiumSchedule[]
            │                                             │
            │  HTTP 201 Created                           │
            │  [ScheduleResponseDto, ...]                 │
            │<────────────────────────────────────────────┤
            ▼                                             ▼
```

#### Feign Client Code in `policy-issuance-service`:
```java
@FeignClient(name = "premium-service")
public interface PremiumServiceClient {

    @PostMapping("/api/v1/premium-schedules")
    ApiResponse<List<ScheduleResponseDto>> generateSchedules(
        @RequestBody ScheduleRequestDto request
    );
}
```

---

### 5.2 Payment Success ➔ Policy Activation & Receipt Dispatch

Once the customer pays their initial installment, `premium-service` verifies the transaction and coordinates with `policy-service` and `notification-service`:

```
┌──────────────┐     ┌─────────────────┐     ┌────────────────┐     ┌──────────────────────┐
│   Customer   │     │ premium-service │     │ policy-service │     │ notification-service │
└──────┬───────┘     └────────┬────────┘     └───────┬────────┘     └──────────┬───────────┘
       │                      │                      │                         │
       │ POST /payments       │                      │                         │
       ├─────────────────────>│                      │                         │
       │                      │ Save Payment         │                         │
       │                      │ Update Schedule PAID │                         │
       │                      │ Generate Receipt     │                         │
       │                      │                      │                         │
       │                      │ PUT /policies/       │                         │
       │                      │ {id}/activate        │                         │
       │                      ├─────────────────────>│                         │
       │                      │<─────────────────────┤                         │
       │                      │                      │                         │
       │                      │ POST /notifications (Async Event)              │
       │                      ├───────────────────────────────────────────────>│
       │                      │                      │                         │ (Sends Email/SMS)
       │ HTTP 201 Success     │                      │                         │
       │<─────────────────────┤                      │                         │
```

---

### 5.3 Claims Management ➔ Premium Eligibility Verification

When a claim is registered (e.g., customer hospitalized), the `claims-service` must ensure the policy is **in good standing** (no unpaid overdue installments):

```java
// Inside ClaimsVerificationService in claims-service:
public boolean isPolicyEligibleForClaim(Long policyId) {
    ApiResponse<List<ScheduleResponseDto>> response = premiumClient.getSchedules(policyId);
    List<ScheduleResponseDto> schedules = response.getData();

    // Check if any past installment is OVERDUE
    boolean hasOverdue = schedules.stream()
        .anyMatch(s -> "OVERDUE".equalsIgnoreCase(s.getStatus()));

    if (hasOverdue) {
        throw new ClaimValidationException("Claim blocked: Policy has overdue premium installments.");
    }
    return true;
}
```

---

### 5.4 Policy Cancellation ➔ Premium Refund Trigger

When a customer submits a cancellation request during the free-look period, `policy-servicing-service` invokes `premium-service` to initiate a refund:

```java
// Inside PolicyCancellationServiceImpl in policy-servicing-service:
public void processCancellation(Long policyId, String reason) {
    // 1. Fetch payment history
    List<PaymentResponseDto> payments = premiumClient.getPaymentHistory(policyId).getData();

    // 2. Identify the refundable amount
    for (PaymentResponseDto payment : payments) {
        RefundRequestDto refundDto = RefundRequestDto.builder()
            .paymentId(payment.getPaymentId())
            .refundAmount(payment.getAmountPaid())
            .reason(reason)
            .build();
        
        // 3. Initiate refund in premium-service
        premiumClient.createRefund(refundDto);
    }
}
```

---

### 5.5 Executive Dashboard ➔ Financial Aggregation

The `reports-service` aggregates real-time metrics across microservices to populate the Executive MIS Dashboard:

```
┌────────────────────┐
│  reports-service   │
└─────────┬──────────┘
          │
          ├────> GET /api/v1/payments/history?date=today  (Total Collection Today)
          ├────> GET /api/v1/premium-schedules/overdue    (Total Overdue Exposure)
          └────> GET /api/v1/refunds?status=APPROVED      (Total Refunds Released)
```

---

## 6. Summary Reference Architecture

```
                                 ENTERPRISE PAS ECOSYSTEM
┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐    ┌───────────────────┐
│ customer-service  │    │    kyc-service    │    │  product-service  │    │   quote-service   │
│ • Dedup Engine    │    │ • Verhoeff Valid. │    │ • Dynamic Rules   │    │ • Actuarial Engine│
│ • Nominee (100%)  │    │ • CKYC Registry   │    │ • Product Vers.   │    │ • 30-Day Expiry   │
└─────────┬─────────┘    └─────────┬─────────┘    └─────────┬─────────┘    └─────────┬─────────┘
          │                        │                        │                        │
          └────────────────────────┴───────────┬────────────┴────────────────────────┘
                                               ▼
                                  ┌─────────────────────────┐
                                  │   underwriting-service  │
                                  │ • STP Auto-Approval     │
                                  │ • BMI & Health Loading  │
                                  └────────────┬────────────┘
                                               ▼
                                  ┌─────────────────────────┐
                                  │     policy-service      │
                                  │ • Number Generator      │
                                  │ • PDF Contract Compiler │
                                  └────────────┬────────────┘
                                               │ Triggers Schedule Generation
                                               ▼
       ═════════════════════════════════════════════════════════════════════════════════
                                   ★ PREMIUM-SERVICE ★
          ┌────────────────────────────────────────────────────────────────────────┐
          │  REST APIs:                                                            │
          │  • /api/v1/premium-schedules    • /api/v1/payments/verify              │
          │  • /api/v1/payments             • /api/v1/payments/{id}/receipt        │
          │  • /api/v1/refunds              • /api/v1/refunds/{id}/approve         │
          │                                                                        │
          │  Autonomous Schedulers:                                                │
          │  • PremiumOverdueScheduler       (Midnight - Grace period & Late fees) │
          │  • PremiumReminderScheduler      (08:00 AM - T-15, T-7, Due, T+3, T+7) │
          │  • PaymentReconciliationScheduler(Every 15 min - Timeout stalled txns) │
          └────────────────────────────────────────────────────────────────────────┘
       ═════════════════════════════════════════════════════════════════════════════════
                                               │
                                               ├────────────────────────┐
                                               ▼                        ▼
                                  ┌─────────────────────────┐ ┌─────────────────────────┐
                                  │      claims-service     │ │ policy-servicing-service│
                                  │ • Net Settlement Calc   │ │ • Financial Endorsement │
                                  │ • Checks OVERDUE status │ │ • Free-look Refund Call │
                                  └─────────────────────────┘ └─────────────────────────┘
```
