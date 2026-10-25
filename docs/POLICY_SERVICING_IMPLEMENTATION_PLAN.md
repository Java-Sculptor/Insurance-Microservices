# Implementation Plan: Policy Servicing & Endorsements Module (`policy-servicing-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 9 (Pages 126–141)  
> **Service Name**: `policy-servicing-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/policy-servicing-service`  
> **Assigned Port**: `8091`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Policy Servicing Module manages all post-issuance customer service requests throughout the 10-30 year lifecycle of a policy: non-financial endorsements (address, nominee), financial endorsements (coverage modification, rider addition/removal), policy renewals, and free-look cancellations.

---

## 2. Database Schema (Tables INS041 – INS046)

### Table INS041: `service_request`
- `request_id` BIGINT PK AUTO_INCREMENT
- `request_number` VARCHAR(30) UNIQUE NOT NULL (Format: `SR-YYYY-XXXXXX`)
- `policy_id` BIGINT NOT NULL
- `customer_id` BIGINT NOT NULL
- `request_type` VARCHAR(100) NOT NULL
- `request_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `status` ENUM('Submitted','Under Review','Approved','Rejected','Completed','Closed') DEFAULT 'Submitted'

### Table INS042: `policy_endorsement`
- `endorsement_id` BIGINT PK AUTO_INCREMENT
- `request_id` BIGINT FK(service_request) NOT NULL
- `endorsement_number` VARCHAR(30) UNIQUE NOT NULL
- `endorsement_type` VARCHAR(100) NOT NULL
- `previous_value` TEXT NOT NULL
- `updated_value` TEXT NOT NULL
- `approval_status` ENUM('Pending','Approved','Rejected') DEFAULT 'Pending'

### Table INS043: `nominee_details`
- `nominee_id` BIGINT PK AUTO_INCREMENT
- `policy_id` BIGINT NOT NULL
- `nominee_name` VARCHAR(100) NOT NULL
- `relationship` VARCHAR(50) NOT NULL
- `dob` DATE NOT NULL
- `share_percentage` DECIMAL(5,2) NOT NULL
- `guardian_name` VARCHAR(100) NULL

### Table INS044: `policy_renewal`
- `renewal_id` BIGINT PK AUTO_INCREMENT
- `policy_id` BIGINT NOT NULL
- `renewal_date` DATE NOT NULL
- `new_expiry_date` DATE NOT NULL
- `premium_amount` DECIMAL(15,2) NOT NULL
- `renewal_status` ENUM('Pending','Paid','Renewed','Expired') DEFAULT 'Pending'

### Table INS045: `policy_cancellation`
- `cancellation_id` BIGINT PK AUTO_INCREMENT
- `policy_id` BIGINT NOT NULL
- `cancellation_date` DATE NOT NULL
- `cancellation_reason` TEXT NOT NULL
- `refund_amount` DECIMAL(15,2) DEFAULT 0.00
- `approved_by` BIGINT NOT NULL (Employee ID)

### Table INS046: `service_request_history`
- `history_id` BIGINT PK AUTO_INCREMENT
- `request_id` BIGINT FK(service_request) NOT NULL
- `action` VARCHAR(100) NOT NULL
- `action_by` BIGINT NOT NULL
- `action_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `remarks` TEXT NULL

---

## 3. All 12 RESTful API Endpoints

### Service Request APIs
1. `POST /api/v1/service-requests` - Create Service Request
2. `GET /api/v1/service-requests` - List Service Requests
3. `GET /api/v1/service-requests/{id}` - View Specific Request
4. `PUT /api/v1/service-requests/{id}` - Update Request Status

### Endorsement Management APIs
5. `POST /api/v1/endorsements` - Create Policy Endorsement
6. `PUT /api/v1/endorsements/{id}/approve` - Approve Endorsement
7. `PUT /api/v1/endorsements/{id}/reject` - Reject Endorsement
8. `GET /api/v1/endorsements/{id}` - View Endorsement Details

### Policy Renewal APIs
9. `POST /api/v1/policies/{id}/renew` - Initiate Policy Renewal
10. `GET /api/v1/renewals/{id}` - View Renewal Details & Schedule

### Policy Cancellation APIs
11. `POST /api/v1/policies/{id}/cancel` - Request Cancellation & Compute Refund
12. `GET /api/v1/cancellations/{id}` - View Cancellation & Refund Status

---

## 4. Key Business Logic Beyond Basic CRUD
* **Active Claim Lock**: Rejects policy cancellation if an active claim is open.
* **Free-Look Refund Trigger**: Automatically calls `premium-service: POST /refunds` if cancellation is within 15 days of policy inception.
* **Before/After Snapshot Versioning**: Captures immutable JSON diffs for legal evidence.
