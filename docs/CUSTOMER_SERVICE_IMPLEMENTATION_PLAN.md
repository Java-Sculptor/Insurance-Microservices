# Implementation Plan: Customer Registration & Profile Module (`customer-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 2 (Pages 14–29)  
> **Service Name**: `customer-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/customer-service`  
> **Assigned Port**: `8082`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Customer Service is the single source of truth for all policyholder identities. It centralizes KYC-ready demographic records, maintains communication & permanent addresses, manages nominee allocations, and stores identification documents.

---

## 2. Database Schema (Tables INS001 – INS005)

### Table INS001: `customer`
- `customer_id` BIGINT PK AUTO_INCREMENT
- `customer_code` VARCHAR(20) UNIQUE NOT NULL (Format: `CUST-YYYY-XXXXXX`)
- `first_name` VARCHAR(100) NOT NULL
- `middle_name` VARCHAR(100) NULL
- `last_name` VARCHAR(100) NOT NULL
- `date_of_birth` DATE NOT NULL
- `gender` ENUM('Male','Female','Other') NOT NULL
- `marital_status` ENUM('Single','Married','Divorced','Widowed') NOT NULL
- `occupation` VARCHAR(100) NOT NULL
- `annual_income` DECIMAL(15,2) NOT NULL
- `mobile_number` VARCHAR(15) UNIQUE NOT NULL
- `email` VARCHAR(150) UNIQUE NOT NULL
- `customer_status` ENUM('Active','Inactive') DEFAULT 'Active'
- `created_at` DATETIME DEFAULT CURRENT_TIMESTAMP
- `updated_at` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP

### Table INS002: `customer_address`
- `address_id` BIGINT PK AUTO_INCREMENT
- `customer_id` BIGINT FK(customer) NOT NULL
- `address_type` ENUM('Permanent','Communication') NOT NULL
- `address_line1` VARCHAR(255) NOT NULL
- `address_line2` VARCHAR(255) NULL
- `city` VARCHAR(100) NOT NULL
- `district` VARCHAR(100) NOT NULL
- `state` VARCHAR(100) NOT NULL
- `country` VARCHAR(100) NOT NULL
- `pincode` VARCHAR(10) NOT NULL

### Table INS003: `nominee`
- `nominee_id` BIGINT PK AUTO_INCREMENT
- `customer_id` BIGINT FK(customer) NOT NULL
- `nominee_name` VARCHAR(150) NOT NULL
- `relationship` VARCHAR(50) NOT NULL
- `date_of_birth` DATE NOT NULL
- `mobile_number` VARCHAR(15) NULL
- `share_percentage` DECIMAL(5,2) NOT NULL
- `guardian_name` VARCHAR(150) NULL (Mandatory if age < 18)
- `guardian_relationship` VARCHAR(50) NULL

### Table INS004: `customer_document`
- `document_id` BIGINT PK AUTO_INCREMENT
- `customer_id` BIGINT FK(customer) NOT NULL
- `document_type` VARCHAR(100) NOT NULL (Aadhaar, PAN, Passport, Driving License, Photo, Signature, Income Proof)
- `file_name` VARCHAR(255) NOT NULL
- `file_path` VARCHAR(500) NOT NULL
- `uploaded_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `verification_status` ENUM('Pending','Verified','Rejected') DEFAULT 'Pending'

### Table INS005: `customer_contact`
- `contact_id` BIGINT PK AUTO_INCREMENT
- `customer_id` BIGINT FK(customer) NOT NULL
- `alternate_mobile` VARCHAR(15) NULL
- `office_phone` VARCHAR(20) NULL
- `preferred_contact_mode` ENUM('Mobile','Email','SMS') DEFAULT 'Mobile'

---

## 3. All 17 RESTful API Endpoints

### Customer APIs
1. `POST /api/v1/customers` - Register New Customer (Triggers deduplication engine)
2. `GET /api/v1/customers` - List All Customers (Pagination + search filters)
3. `GET /api/v1/customers/{id}` - Get Customer By ID (Full profile with addresses & nominees)
4. `PUT /api/v1/customers/{id}` - Update Customer Profile
5. `DELETE /api/v1/customers/{id}` - Soft Delete Customer (`status = Inactive`)
6. `GET /api/v1/customers/search` - Search Customer (by mobile, email, PAN, Aadhaar, name)

### Address APIs
7. `POST /api/v1/customers/{id}/addresses` - Add Address (Permanent/Communication)
8. `GET /api/v1/customers/{id}/addresses` - List Addresses for Customer
9. `PUT /api/v1/addresses/{addressId}` - Update Address
10. `DELETE /api/v1/addresses/{addressId}` - Delete Address

### Nominee APIs
11. `POST /api/v1/customers/{id}/nominees` - Add Nominee (Validates total share <= 100%)
12. `GET /api/v1/customers/{id}/nominees` - List Nominees for Customer
13. `PUT /api/v1/nominees/{nomineeId}` - Update Nominee
14. `DELETE /api/v1/nominees/{nomineeId}` - Delete Nominee

### Document APIs
15. `POST /api/v1/customers/{id}/documents` - Upload Customer Document
16. `GET /api/v1/customers/{id}/documents` - List Documents for Customer
17. `PUT /api/v1/documents/{documentId}/verify` - Mark Document Verified/Rejected

---

## 4. Key Business Logic Beyond Basic CRUD
* **Deduplication Engine**: Evaluates `First Name + Last Name + DOB + Mobile + Email`. Rejects duplicate registration.
* **100% Nominee Share Guard**: Ensures sum of all nominee percentages for a customer equals 100.00%.
* **Minor Protection**: Minor nominees (< 18 years) mandate guardian details.
