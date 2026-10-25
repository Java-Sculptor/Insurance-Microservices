# Implementation Plan: Quote Generation & Actuarial Module (`quote-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 5 (Pages 60–77)  
> **Service Name**: `quote-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/quote-service`  
> **Assigned Port**: `8085`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Quote Service executes actuarial premium mathematics. It takes customer demographic inputs, evaluates initial risk tier, applies product rates, bundles riders, applies discount coupons, adds 18% GST, and locks the quotation for 30 calendar days.

---

## 2. Database Schema (Tables INS018 – INS023)

### Table INS018: `quote`
- `quote_id` BIGINT PK AUTO_INCREMENT
- `quote_number` VARCHAR(30) UNIQUE NOT NULL (Format: `QT-YYYY-XXXXXX`)
- `customer_id` BIGINT NOT NULL (FK Customer)
- `product_id` BIGINT NOT NULL (FK Product)
- `policy_term` INT NOT NULL (Months / Years)
- `sum_insured` DECIMAL(15,2) NOT NULL
- `premium_frequency_id` BIGINT NOT NULL
- `quote_date` DATE NOT NULL
- `expiry_date` DATE NOT NULL (quote_date + 30 days)
- `quote_status` ENUM('Pending','Approved','Rejected','Expired','Converted') DEFAULT 'Pending'

### Table INS019: `premium_calculation`
- `calculation_id` BIGINT PK AUTO_INCREMENT
- `quote_id` BIGINT FK(quote) NOT NULL
- `base_premium` DECIMAL(15,2) NOT NULL
- `risk_loading` DECIMAL(15,2) DEFAULT 0.00
- `rider_premium` DECIMAL(15,2) DEFAULT 0.00
- `discount_amount` DECIMAL(15,2) DEFAULT 0.00
- `gst_amount` DECIMAL(15,2) NOT NULL (18%)
- `final_premium` DECIMAL(15,2) NOT NULL

### Table INS020: `risk_assessment`
- `risk_id` BIGINT PK AUTO_INCREMENT
- `quote_id` BIGINT FK(quote) NOT NULL
- `age_score` INT NOT NULL (0-20)
- `occupation_score` INT NOT NULL (0-20)
- `medical_score` INT NOT NULL (0-30)
- `lifestyle_score` INT NOT NULL (0-20)
- `total_score` INT NOT NULL (0-100)
- `risk_category` ENUM('Low','Medium','High') NOT NULL

### Table INS021: `quote_rider`
- `rider_selection_id` BIGINT PK AUTO_INCREMENT
- `quote_id` BIGINT FK(quote) NOT NULL
- `rider_id` BIGINT NOT NULL
- `rider_premium` DECIMAL(15,2) NOT NULL

### Table INS022: `discount_master`
- `discount_id` BIGINT PK AUTO_INCREMENT
- `discount_name` VARCHAR(100) NOT NULL (Online, Corporate, Festival, NCB)
- `discount_percentage` DECIMAL(5,2) NOT NULL
- `valid_from` DATE NOT NULL
- `valid_to` DATE NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS023: `quote_history`
- `history_id` BIGINT PK AUTO_INCREMENT
- `quote_id` BIGINT FK(quote) NOT NULL
- `action` VARCHAR(100) NOT NULL
- `performed_by` BIGINT NOT NULL
- `action_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `remarks` VARCHAR(500) NULL

---

## 3. All 12 RESTful API Endpoints

### Core Quote APIs
1. `POST /api/v1/quotes` - Generate New Quote (Evaluates risk & runs premium formula)
2. `GET /api/v1/quotes` - Get All Quotes (Filter by customer, date, status)
3. `GET /api/v1/quotes/{id}` - Get Quote Details with calculation breakdown
4. `PUT /api/v1/quotes/{id}` - Update Quote (Adjust cover, term, or riders)
5. `DELETE /api/v1/quotes/{id}` - Cancel Quote

### Actuarial Calculation APIs
6. `POST /api/v1/quotes/calculate-premium` - Ad-hoc Premium Simulation
7. `GET /api/v1/quotes/{id}/premium` - Get Detailed Breakdown (Base, Loading, Riders, GST)

### Risk Assessment APIs
8. `POST /api/v1/risk-assessment` - Assess Customer Risk Profile
9. `GET /api/v1/risk-assessment/{quoteId}` - View Risk Scoring Matrix

### Quote Governance & Conversion APIs
10. `PUT /api/v1/quotes/{id}/approve` - Approve High-Value Quote
11. `PUT /api/v1/quotes/{id}/reject` - Reject Quote
12. `PUT /api/v1/quotes/{id}/convert` - Convert Approved Quote to Policy Proposal

---

## 4. Key Business Logic Beyond Basic CRUD
* **Actuarial Formula Engine**:
  $$	ext{Base} = (	ext{Sum Insured} 	imes 	ext{Rate}) / 1000$$
  $$	ext{Final} = 	ext{Base} + 	ext{Loading} + 	ext{Riders} - 	ext{Discount} + 18\% 	ext{ GST}$$
* **30-Day Legal Price Lock**: Strict rejection of quote conversion if current date > `expiry_date`.
* **High-Risk Threshold**: If Total Risk Score $> 70$, marks quote as `High Risk`, mandating senior underwriter approval.
