# Implementation Plan: KYC Verification & Compliance Module (`kyc-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 3 (Pages 29–43)  
> **Service Name**: `kyc-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/kyc-service`  
> **Assigned Port**: `8083`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The KYC Verification Module is a mandatory regulatory compliance gate under IRDAI and Anti-Money Laundering (AML) laws. It validates government IDs (Aadhaar, PAN, Passport, Driving License), integrates with Central KYC (CKYC) registry, enforces document verification workflows, and logs an immutable audit trail. Policy issuance and quotes are blocked until KYC is `VERIFIED`.

---

## 2. Database Schema (Tables INS006 – INS010)

### Table INS006: `customer_kyc`
- `kyc_id` BIGINT PK AUTO_INCREMENT
- `customer_id` BIGINT FK(customer) NOT NULL
- `kyc_number` VARCHAR(30) UNIQUE NOT NULL (Format: `KYC-YYYY-XXXXXX`)
- `kyc_date` DATE NOT NULL
- `verification_status` ENUM('Pending','Verified','Rejected','Re-Submitted') DEFAULT 'Pending'
- `remarks` VARCHAR(500) NULL
- `verified_by` BIGINT NULL (Employee ID)
- `verified_date` DATETIME NULL

### Table INS007: `kyc_document`
- `document_id` BIGINT PK AUTO_INCREMENT
- `kyc_id` BIGINT FK(customer_kyc) NOT NULL
- `document_type` ENUM('Aadhaar','PAN','Passport','Driving License','Voter ID','Address Proof') NOT NULL
- `document_number` VARCHAR(100) NOT NULL
- `file_path` VARCHAR(500) NOT NULL
- `upload_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `verification_status` ENUM('Pending','Verified','Rejected') DEFAULT 'Pending'

### Table INS008: `ckyc_details`
- `ckyc_id` BIGINT PK AUTO_INCREMENT
- `customer_id` BIGINT FK(customer) NOT NULL
- `ckyc_number` VARCHAR(30) UNIQUE NOT NULL
- `verification_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `ckyc_status` ENUM('Verified','Not Found','Pending') DEFAULT 'Pending'

### Table INS009: `kyc_verification_log`
- `log_id` BIGINT PK AUTO_INCREMENT
- `kyc_id` BIGINT FK(customer_kyc) NOT NULL
- `action` VARCHAR(100) NOT NULL
- `performed_by` BIGINT NOT NULL (Employee ID)
- `action_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `remarks` VARCHAR(500) NULL

### Table INS010: `document_type_master`
- `document_type_id` BIGINT PK AUTO_INCREMENT
- `document_name` VARCHAR(100) UNIQUE NOT NULL
- `mandatory_flag` BOOLEAN DEFAULT TRUE
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

---

## 3. All 14 RESTful API Endpoints

### Core KYC APIs
1. `POST /api/v1/kyc` - Create KYC Request
2. `GET /api/v1/kyc` - List KYC Requests (Filter by status, date)
3. `GET /api/v1/kyc/{kycId}` - Get Full KYC Details
4. `PUT /api/v1/kyc/{kycId}` - Update KYC Information
5. `DELETE /api/v1/kyc/{kycId}` - Cancel KYC Request

### Document Verification APIs
6. `POST /api/v1/kyc/documents` - Upload KYC Document
7. `GET /api/v1/kyc/documents/{documentId}` - View/Download Document
8. `PUT /api/v1/kyc/documents/{documentId}/verify` - Verify Document
9. `PUT /api/v1/kyc/documents/{documentId}/reject` - Reject Document with Reason

### Central KYC (CKYC) APIs
10. `POST /api/v1/kyc/ckyc/verify` - Check & Verify Central CKYC Registry
11. `GET /api/v1/kyc/ckyc/{customerId}` - Get CKYC Demographic Record

### Approval Workflow APIs
12. `PUT /api/v1/kyc/{kycId}/approve` - Compliance Approval of KYC
13. `PUT /api/v1/kyc/{kycId}/reject` - Compliance Rejection of KYC
14. `PUT /api/v1/kyc/{kycId}/resubmit` - Customer Re-submits after rejection

---

## 4. Key Business Logic Beyond Basic CRUD
* **Verhoeff Checksum Algorithm**: Validates 12-digit Aadhaar before initiating external network verification.
* **PAN Formatting Check**: Regex `[A-Z]{5}[0-9]{4}[A-Z]{1}` validating 4th letter entity type.
* **One Active KYC Rule**: Strict database unique constraint ensuring only 1 active KYC request per customer.
* **Compliance Gate**: Exposes boolean check consumed by Quote and Underwriting services.
