# Implementation Plan: Insurance Product Management Module (`product-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 4 (Pages 43–60)  
> **Service Name**: `product-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/product-service`  
> **Assigned Port**: `8084`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Product Service defines insurance plans (Life, Health, Motor, Travel, Property). It manages product eligibility rules, coverage limits, allowable riders, waiting periods, premium frequencies, and commission structures.

---

## 2. Database Schema (Tables INS011 – INS017)

### Table INS011: `product_category`
- `category_id` BIGINT PK AUTO_INCREMENT
- `category_name` VARCHAR(100) UNIQUE NOT NULL (Life, Health, Motor, Travel, Property)
- `description` VARCHAR(255) NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS012: `insurance_product`
- `product_id` BIGINT PK AUTO_INCREMENT
- `category_id` BIGINT FK(product_category) NOT NULL
- `product_code` VARCHAR(30) UNIQUE NOT NULL
- `product_name` VARCHAR(150) NOT NULL
- `product_type` VARCHAR(100) NOT NULL
- `description` TEXT NULL
- `minimum_age` INT NOT NULL
- `maximum_age` INT NOT NULL
- `minimum_sum_insured` DECIMAL(15,2) NOT NULL
- `maximum_sum_insured` DECIMAL(15,2) NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS013: `coverage_master`
- `coverage_id` BIGINT PK AUTO_INCREMENT
- `product_id` BIGINT FK(insurance_product) NOT NULL
- `coverage_name` VARCHAR(150) NOT NULL
- `minimum_coverage` DECIMAL(15,2) NOT NULL
- `maximum_coverage` DECIMAL(15,2) NOT NULL
- `waiting_period_days` INT DEFAULT 0
- `deductible_amount` DECIMAL(15,2) DEFAULT 0.00

### Table INS014: `rider_master`
- `rider_id` BIGINT PK AUTO_INCREMENT
- `product_id` BIGINT FK(insurance_product) NOT NULL
- `rider_name` VARCHAR(150) NOT NULL (Accidental Death, Critical Illness, Waiver of Premium, etc.)
- `rider_description` TEXT NULL
- `rider_premium` DECIMAL(15,2) NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS015: `benefit_master`
- `benefit_id` BIGINT PK AUTO_INCREMENT
- `product_id` BIGINT FK(insurance_product) NOT NULL
- `benefit_name` VARCHAR(150) NOT NULL (Hospital Cash, Disability, Maturity Benefit)
- `benefit_description` TEXT NULL
- `benefit_limit` DECIMAL(15,2) NOT NULL

### Table INS016: `premium_frequency`
- `frequency_id` BIGINT PK AUTO_INCREMENT
- `frequency_name` ENUM('Monthly','Quarterly','Half-Yearly','Yearly','Single Premium') NOT NULL
- `installment_count` INT NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS017: `product_commission`
- `commission_id` BIGINT PK AUTO_INCREMENT
- `product_id` BIGINT FK(insurance_product) NOT NULL
- `first_year_commission` DECIMAL(5,2) NOT NULL
- `renewal_commission` DECIMAL(5,2) NOT NULL
- `branch_commission` DECIMAL(5,2) NOT NULL

---

## 3. All 18 RESTful API Endpoints

### Product APIs
1. `POST /api/v1/products` - Create Insurance Product
2. `GET /api/v1/products` - Get All Products (Filter by category, status)
3. `GET /api/v1/products/{id}` - Get Product By ID (Full rules, coverages, riders)
4. `PUT /api/v1/products/{id}` - Update Product Configuration
5. `DELETE /api/v1/products/{id}` - Deactivate Product (Soft-delete only)

### Category APIs
6. `POST /api/v1/product-categories` - Create Category
7. `GET /api/v1/product-categories` - List Categories
8. `PUT /api/v1/product-categories/{id}` - Update Category
9. `DELETE /api/v1/product-categories/{id}` - Deactivate Category

### Coverage APIs
10. `POST /api/v1/products/{id}/coverages` - Add Coverage Option
11. `GET /api/v1/products/{id}/coverages` - List Coverages
12. `PUT /api/v1/coverages/{coverageId}` - Update Coverage
13. `DELETE /api/v1/coverages/{coverageId}` - Delete Coverage

### Rider APIs
14. `POST /api/v1/products/{id}/riders` - Add Optional Rider
15. `GET /api/v1/products/{id}/riders` - List Riders
16. `PUT /api/v1/riders/{riderId}` - Update Rider
17. `DELETE /api/v1/riders/{riderId}` - Delete Rider

### Benefit APIs
18. `POST /api/v1/products/{id}/benefits` - Add Benefit to Product

---

## 4. Key Business Logic Beyond Basic CRUD
* **Active Policy Protection Guard**: Products with active issued policies cannot be deleted or have existing cover limits reduced.
* **Rider Mutual Exclusion Rule**: Prevents invalid cross-category combinations (e.g. Critical Illness on Vehicle insurance).
* **Multi-frequency Mapping**: Links allowable frequencies (Monthly/Yearly/etc.) per product tier.
