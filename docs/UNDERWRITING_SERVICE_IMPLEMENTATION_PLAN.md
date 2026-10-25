# Implementation Plan: Underwriting & Decision Engine (`underwriting-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 6 (Pages 77–94)  
> **Service Name**: `underwriting-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/underwriting-service`  
> **Assigned Port**: `8086`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Underwriting Service is the financial risk evaluation engine. It assesses medical health, evaluates financial solvency (Human Life Value), calculates premium loadings for high-risk profiles, operates Straight-Through Processing (STP) for instant auto-approval, and routes complex cases to human underwriters.

---

## 2. Database Schema (Tables INS024 – INS029)

### Table INS024: `underwriting_case`
- `underwriting_id` BIGINT PK AUTO_INCREMENT
- `quote_id` BIGINT NOT NULL (FK Quote)
- `customer_id` BIGINT NOT NULL (FK Customer)
- `product_id` BIGINT NOT NULL (FK Product)
- `underwriting_date` DATE NOT NULL
- `status` ENUM('Pending','Approved','Rejected','Medical Pending','Financial Pending') DEFAULT 'Pending'

### Table INS025: `medical_underwriting`
- `medical_id` BIGINT PK AUTO_INCREMENT
- `underwriting_id` BIGINT FK(underwriting_case) NOT NULL
- `height` DECIMAL(5,2) NOT NULL (cm)
- `weight` DECIMAL(5,2) NOT NULL (kg)
- `bmi` DECIMAL(5,2) NOT NULL
- `smoking` BOOLEAN DEFAULT FALSE
- `alcohol` BOOLEAN DEFAULT FALSE
- `medical_history` TEXT NULL
- `medical_report_path` VARCHAR(500) NULL

### Table INS026: `financial_underwriting`
- `financial_id` BIGINT PK AUTO_INCREMENT
- `underwriting_id` BIGINT FK(underwriting_case) NOT NULL
- `annual_income` DECIMAL(15,2) NOT NULL
- `employer_name` VARCHAR(150) NOT NULL
- `occupation` VARCHAR(100) NOT NULL
- `existing_insurance` DECIMAL(15,2) DEFAULT 0.00
- `loan_liability` DECIMAL(15,2) DEFAULT 0.00

### Table INS027: `risk_score`
- `risk_id` BIGINT PK AUTO_INCREMENT
- `underwriting_id` BIGINT FK(underwriting_case) NOT NULL
- `medical_score` INT NOT NULL (0-30)
- `occupation_score` INT NOT NULL (0-20)
- `lifestyle_score` INT NOT NULL (0-20)
- `financial_score` INT NOT NULL (0-10)
- `total_score` INT NOT NULL (0-100)
- `risk_category` ENUM('Low','Medium','High','Very High') NOT NULL

### Table INS028: `underwriting_decision`
- `decision_id` BIGINT PK AUTO_INCREMENT
- `underwriting_id` BIGINT FK(underwriting_case) NOT NULL
- `decision` ENUM('Approved','Approved with Loading','Approved with Exclusions','Rejected') NOT NULL
- `loading_percentage` DECIMAL(5,2) DEFAULT 0.00
- `exclusion_details` TEXT NULL
- `rejection_reason` TEXT NULL
- `approved_by` BIGINT NOT NULL (Employee ID)
- `decision_date` DATETIME DEFAULT CURRENT_TIMESTAMP

### Table INS029: `underwriting_audit_log`
- `audit_id` BIGINT PK AUTO_INCREMENT
- `underwriting_id` BIGINT FK(underwriting_case) NOT NULL
- `action` VARCHAR(100) NOT NULL
- `performed_by` BIGINT NOT NULL (Employee ID)
- `remarks` TEXT NULL
- `action_date` DATETIME DEFAULT CURRENT_TIMESTAMP

---

## 3. All 14 RESTful API Endpoints

### Case Management APIs
1. `POST /api/v1/underwriting` - Create Underwriting Case (from approved quote)
2. `GET /api/v1/underwriting` - List Underwriting Cases (Filter by status, risk)
3. `GET /api/v1/underwriting/{id}` - Get Comprehensive Underwriting Details
4. `PUT /api/v1/underwriting/{id}` - Update Case Details

### Medical Underwriting APIs
5. `POST /api/v1/underwriting/{id}/medical` - Submit Medical Details & Auto-calculate BMI
6. `GET /api/v1/underwriting/{id}/medical` - View Medical Evaluation
7. `PUT /api/v1/underwriting/{id}/medical` - Update Medical Evaluation

### Financial Underwriting APIs
8. `POST /api/v1/underwriting/{id}/financial` - Submit Financial Details & Income Proof
9. `GET /api/v1/underwriting/{id}/financial` - View Financial Assessment
10. `PUT /api/v1/underwriting/{id}/financial` - Update Financial Assessment

### Decision & Approval APIs
11. `PUT /api/v1/underwriting/{id}/approve` - Approve Proposal (Triggers Policy Issuance)
12. `PUT /api/v1/underwriting/{id}/reject` - Reject Proposal with mandatory legal reason
13. `PUT /api/v1/underwriting/{id}/loading` - Apply Additional Premium Loading
14. `GET /api/v1/underwriting/{id}/decision` - View Final Decision & Loading Breakdown

---

## 4. Key Business Logic Beyond Basic CRUD
* **STP Auto-Approval**: Low risk cases (Score <= 30, Sum Insured < ₹25,00,000) are auto-approved in milliseconds.
* **BMI & Loading Engine**: Automatic calculation of BMI. Scores > 32 trigger mandatory 10%–25% loading.
* **Human Life Value Guard**: Validates that Sum Insured does not exceed 20x annual income.
