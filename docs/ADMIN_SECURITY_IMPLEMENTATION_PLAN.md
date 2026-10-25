# Implementation Plan: Administration & Security Module (`admin-service` / `auth-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 12 (Pages 177–199)  
> **Service Name**: `admin-service` (or `auth-service`)  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/admin-service`  
> **Assigned Port**: `8080` / `8081`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Administration & Security Module provides centralized control over authentication (JWT), user management, Role-Based Access Control (RBAC), branch/department masters, notification templates, and immutable audit logs across the PAS microservices.

---

## 2. Database Schema (Tables INS059 – INS068)

### Table INS059: `user_master`
- `user_id` BIGINT PK AUTO_INCREMENT
- `employee_id` BIGINT NULL
- `username` VARCHAR(100) UNIQUE NOT NULL
- `password_hash` VARCHAR(255) NOT NULL (BCrypt)
- `email` VARCHAR(150) UNIQUE NOT NULL
- `mobile` VARCHAR(20) NOT NULL
- `role_id` BIGINT NOT NULL (FK Role_Master)
- `branch_id` BIGINT NOT NULL (FK Branch_Master)
- `status` ENUM('Active','Inactive','Locked') DEFAULT 'Active'

### Table INS060: `role_master`
- `role_id` BIGINT PK AUTO_INCREMENT
- `role_name` VARCHAR(100) UNIQUE NOT NULL (Super Admin, Underwriter, Claims Manager, etc.)
- `description` VARCHAR(255) NULL

### Table INS061: `permission_master`
- `permission_id` BIGINT PK AUTO_INCREMENT
- `permission_name` VARCHAR(100) NOT NULL
- `module_name` VARCHAR(100) NOT NULL
- `action_type` VARCHAR(50) NOT NULL (Create, Read, Update, Delete, Approve, Export)

### Table INS062: `role_permission`
- `role_permission_id` BIGINT PK AUTO_INCREMENT
- `role_id` BIGINT FK(role_master) NOT NULL
- `permission_id` BIGINT FK(permission_master) NOT NULL

### Table INS063: `branch_master`
- `branch_id` BIGINT PK AUTO_INCREMENT
- `branch_code` VARCHAR(20) UNIQUE NOT NULL
- `branch_name` VARCHAR(150) NOT NULL
- `city` VARCHAR(100) NOT NULL
- `state` VARCHAR(100) NOT NULL
- `address` VARCHAR(255) NOT NULL
- `manager_id` BIGINT NULL

### Table INS064: `department_master`
- `department_id` BIGINT PK AUTO_INCREMENT
- `department_name` VARCHAR(100) UNIQUE NOT NULL
- `description` VARCHAR(255) NULL

### Table INS065: `master_data`
- `master_id` BIGINT PK AUTO_INCREMENT
- `master_type` VARCHAR(100) NOT NULL (Gender, Occupation, PolicyStatus, etc.)
- `master_value` VARCHAR(150) NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS066: `notification_template`
- `template_id` BIGINT PK AUTO_INCREMENT
- `template_name` VARCHAR(150) NOT NULL
- `notification_type` ENUM('Email','SMS','WhatsApp','Push') NOT NULL
- `subject` VARCHAR(255) NULL
- `body` TEXT NOT NULL
- `version_no` INT DEFAULT 1
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS067: `system_configuration`
- `config_id` BIGINT PK AUTO_INCREMENT
- `config_key` VARCHAR(100) UNIQUE NOT NULL
- `config_value` VARCHAR(255) NOT NULL
- `description` VARCHAR(255) NULL

### Table INS068: `audit_log`
- `audit_id` BIGINT PK AUTO_INCREMENT
- `user_id` BIGINT NOT NULL
- `module_name` VARCHAR(100) NOT NULL
- `action` VARCHAR(100) NOT NULL
- `old_value` TEXT NULL
- `new_value` TEXT NULL
- `ip_address` VARCHAR(50) NULL
- `action_time` DATETIME DEFAULT CURRENT_TIMESTAMP

---

## 3. All 20 RESTful API Endpoints

### Authentication APIs
1. `POST /api/v1/auth/login` - Authenticate User & Issue JWT Access + Refresh Tokens
2. `POST /api/v1/auth/logout` - Invalidate Refresh Token
3. `POST /api/v1/auth/refresh-token` - Issue New Access Token
4. `POST /api/v1/auth/change-password` - User Password Change
5. `POST /api/v1/auth/forgot-password` - Trigger Password Reset Email
6. `POST /api/v1/auth/reset-password` - Submit New Password via Reset Token

### User Administration APIs
7. `POST /api/v1/users` - Create Application User
8. `GET /api/v1/users` - List Users
9. `GET /api/v1/users/{id}` - View User Details
10. `PUT /api/v1/users/{id}` - Update User
11. `DELETE /api/v1/users/{id}` - Deactivate User
12. `PUT /api/v1/users/{id}/lock` - Lock User Account
13. `PUT /api/v1/users/{id}/unlock` - Unlock User Account

### RBAC APIs
14. `POST /api/v1/roles` - Create Role
15. `GET /api/v1/roles` - List Roles
16. `POST /api/v1/permissions` - Create Permission
17. `PUT /api/v1/roles/{id}/permissions` - Assign Permissions to Role

### System Configuration & Master APIs
18. `POST /api/v1/branches` - Create Branch
19. `POST /api/v1/masters` - Create Master Data Entry
20. `GET /api/v1/audit-logs` - Query Immutable Security Audit Trail

---

## 4. Key Security & Architectural Guardrails
* **BCrypt Hashing**: Passwords stored with high salt rounds; never plaintext.
* **Brute-Force Lockout**: 5 consecutive failed login attempts automatically transitions user status to `LOCKED`.
* **Tamper-Proof Audit Logging**: `audit_log` table allows only `INSERT` and `SELECT`; updates and deletes are blocked.
