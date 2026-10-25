# Implementation Plan: Reports & Analytics Module (`reports-service`)

> **Module Ref**: Business Requirement Document (BRD) — Section 11 (Pages 161–177)  
> **Service Name**: `reports-service`  
> **Target Location**: `/Users/apple/Workspace/team 11/Insurance-Microservices/reports-service`  
> **Assigned Port**: `8092`  
> **Status**: Planned (Do Not Implement Yet)

---

## 1. Domain Overview
The Reports & Analytics Module consolidates operational and financial intelligence across Customer, Policy, Premium, Underwriting, and Claims services. It generates MIS reports, computes executive KPIs (Loss Ratio, Collection Efficiency), powers graphical dashboards, and exports data to PDF/Excel/CSV.

---

## 2. Database Schema (Tables INS054 – INS058)

### Table INS054: `report_master`
- `report_id` BIGINT PK AUTO_INCREMENT
- `report_name` VARCHAR(150) NOT NULL
- `report_type` VARCHAR(100) NOT NULL
- `description` TEXT NULL
- `created_date` DATETIME DEFAULT CURRENT_TIMESTAMP

### Table INS055: `report_schedule`
- `schedule_id` BIGINT PK AUTO_INCREMENT
- `report_id` BIGINT FK(report_master) NOT NULL
- `schedule_type` ENUM('Daily','Weekly','Monthly','Quarterly','Yearly') NOT NULL
- `next_run_date` DATETIME NOT NULL
- `email_to` VARCHAR(250) NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS056: `report_execution`
- `execution_id` BIGINT PK AUTO_INCREMENT
- `report_id` BIGINT FK(report_master) NOT NULL
- `executed_by` BIGINT NOT NULL (Employee ID)
- `execution_date` DATETIME DEFAULT CURRENT_TIMESTAMP
- `execution_status` ENUM('Success','Failed') NOT NULL
- `file_path` VARCHAR(500) NULL

### Table INS057: `dashboard_widget`
- `widget_id` BIGINT PK AUTO_INCREMENT
- `widget_name` VARCHAR(100) NOT NULL
- `widget_type` VARCHAR(50) NOT NULL (Line, Bar, Pie, MetricCard)
- `sequence_no` INT NOT NULL
- `status` ENUM('Active','Inactive') DEFAULT 'Active'

### Table INS058: `analytics_snapshot`
- `snapshot_id` BIGINT PK AUTO_INCREMENT
- `snapshot_date` DATE NOT NULL
- `total_customers` INT NOT NULL
- `total_policies` INT NOT NULL
- `premium_collected` DECIMAL(15,2) NOT NULL
- `claims_paid` DECIMAL(15,2) NOT NULL
- `revenue` DECIMAL(15,2) NOT NULL

---

## 3. All 16 RESTful API Endpoints

### Report Generation APIs
1. `GET /api/v1/reports/customers` - Customer Growth & Demographic Reports
2. `GET /api/v1/reports/policies` - Policy Issuance, Active & Renewal Reports
3. `GET /api/v1/reports/premiums` - Daily, Monthly & Overdue Premium Reports
4. `GET /api/v1/reports/claims` - Claims Ratio, Settlement & Fraud Reports
5. `GET /api/v1/reports/underwriting` - Underwriting Turnaround & Loading Reports
6. `GET /api/v1/reports/agents` - Agent Sales & Commission Performance
7. `GET /api/v1/reports/financial` - Revenue, Profit & Loss Breakdown

### Dashboard & Analytics APIs
8. `GET /api/v1/dashboard/summary` - Executive Summary Metrics
9. `GET /api/v1/dashboard/charts` - Data series for charts (Line, Pie, Bar)
10. `GET /api/v1/dashboard/kpi` - Key Performance Indicators (Loss Ratio, Collection Efficiency)

### Export APIs
11. `GET /api/v1/reports/export/pdf` - Export Report as formatted PDF
12. `GET /api/v1/reports/export/excel` - Export Report as Excel Workbook (.xlsx)
13. `GET /api/v1/reports/export/csv` - Stream Raw CSV Data

### Scheduled Report APIs
14. `POST /api/v1/reports/schedule` - Schedule Automated Recurring Report
15. `PUT /api/v1/reports/schedule/{id}` - Update Schedule
16. `DELETE /api/v1/reports/schedule/{id}` - Delete Schedule

---

## 4. Key Business Logic Beyond Basic CRUD
* **Actuarial KPI Computation**:
  $$	ext{Loss Ratio} = (	ext{Total Claims Paid} / 	ext{Total Premium Collected}) 	imes 100$$
  $$	ext{Collection Efficiency} = (	ext{Actual Collected} / 	ext{Scheduled Due}) 	imes 100$$
* **Asynchronous Big-Data Streaming**: Large reports (> 10,000 rows) are generated asynchronously in background threads with execution history tracking.
