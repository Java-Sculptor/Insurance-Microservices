# 📊 Reports & Analytics Service: Comprehensive Architectural Guide, Real-World Story, Entity Schema & Line-by-Line ServiceImpl Deep Dive

---

## 🌟 Table of Contents
1. [The Real-World Story: The Navigational Cockpit of an Insurer](#1-the-real-world-story-the-navigational-cockpit-of-an-insurer)
   - [The Blind Titanic Disaster vs. Real-Time Actuarial Analytics](#the-blind-titanic-disaster-vs-real-time-actuarial-analytics)
   - [Why Reports Service Protects the Insurer from Insolvency](#why-reports-service-protects-the-insurer-from-insolvency)
2. [Comprehensive Entity Architecture (The 5 Tables)](#2-comprehensive-entity-architecture-the-5-tables)
   - [1. ReportMaster Entity (INS054 - Report Catalog)](#1-reportmaster-entity-ins054---report-catalog)
   - [2. ReportSchedule Entity (INS055 - Autonomous Cron Schedules)](#2-reportschedule-entity-ins055---autonomous-cron-schedules)
   - [3. ReportExecution Entity (INS056 - Execution History & Artifacts)](#3-reportexecution-entity-ins056---execution-history--artifacts)
   - [4. DashboardWidget Entity (INS057 - Executive Visualizations)](#4-dashboardwidget-entity-ins057---executive-visualizations)
   - [5. AnalyticsSnapshot Entity (INS058 - Pre-Aggregated Daily Ledger)](#5-analyticssnapshot-entity-ins058---pre-aggregated-daily-ledger)
3. [Master Endpoints Directory (16 REST APIs)](#3-master-endpoints-directory-16-rest-apis)
4. [Phase-by-Phase Line-by-Line ServiceImpl Business Logic Deep Dive](#4-phase-by-phase-line-by-line-serviceimpl-business-logic-deep-dive)
   - [Phase 1: Actuarial Loss Ratio & Combined Ratio Formula Engine](#phase-1-actuarial-loss-ratio--combined-ratio-formula-engine)
   - [Phase 2: Premium Collection Efficiency Metric Engine](#phase-2-premium-collection-efficiency-metric-engine)
   - [Phase 3: High-Volume Asynchronous PDF/Excel Streaming](#phase-3-high-volume-asynchronous-pdfexcel-streaming)
5. [Senior Developer Viva & Architecture Defense Cheat Sheet](#5-senior-developer-viva--architecture-defense-cheat-sheet)

---

## 1. The Real-World Story: The Navigational Cockpit of an Insurer

### 📖 The Blind Titanic Disaster
In 2017, a commercial motor insurer expanded aggressively, offering 40% discounts on commercial heavy truck policies to capture market share. Gross written premium looked fantastic: ₹400 Crores collected in 9 months. The board celebrated.

However, the company lacked a real-time **Reports & Analytics Service**. Reports were compiled manually once a year by spreadsheet clerks. Ten months later, the annual loss ratio was finally computed:
$$\text{Loss Ratio} = \frac{\text{Claims Paid (₹580 Crores)}}{\text{Net Premium Collected (₹350 Crores)}} = \mathbf{165.7\%}$$
For every ₹100 of premium the company collected, it was paying out ₹165 in crushed truck claims and third-party liabilities! The company burned through its statutory capital reserves, breached solvency margins, was placed under administrative supervision by the regulator, and narrowly avoided total insolvency.

---

### Why Reports Service Protects the Insurer from Insolvency
Insurance is an industry where **revenue is collected today, but costs (claims) occur years into the future**. If an insurer cannot measure its loss ratios and collection efficiencies in real-time, it is flying a jumbo jet in thick fog without altimeters or radar.

**`reports-service`** continuously monitors:
1. **Loss Ratio (Claims Paid / Earned Premium)**: Flags if a specific vehicle model or hospital network is bleeding money.
2. **Collection Efficiency (Collected / Due)**: Tracks how many quarterly installments were missed.
3. **Persistency Ratio (13th Month / 37th Month)**: Measures how many customers renewed their policies.

---

## 2. Comprehensive Entity Architecture (The 5 Tables)

| Table & Entity | Key Fields & Constraints | Real-World Purpose |
| :--- | :--- | :--- |
| **`report_master`** (INS054) | `report_code` (UK), `report_name`, `module_name` | Catalog of statutory and operational reports. |
| **`report_schedule`** (INS055)| `schedule_type`, `next_run_date`, `email_to`, `cron_expr` | Governs automated recurring report distribution. |
| **`report_execution`** (INS056)| `execution_status`, `file_path`, `row_count` | Execution log tracking report generation duration and storage paths. |
| **`dashboard_widget`** (INS057)| `widget_name`, `widget_type` (MetricCard, Line, Pie) | Executive visualization cards for C-suite dashboards. |
| **`analytics_snapshot`** (INS058)| `snapshot_date`, `total_customers`, `premium_collected`, `claims_paid` | Pre-computed nightly snapshots avoiding expensive cross-table table scans. |

---

## 3. Phase-by-Phase Line-by-Line ServiceImpl Business Logic Deep Dive

#### Actuarial KPI Computation Engine
```java
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class AnalyticsServiceImpl implements AnalyticsService {

    private final ClaimClient claimClient;     // OpenFeign to claim-service
    private final PremiumClient premiumClient; // OpenFeign to premium-service
    private final PolicyClient policyClient;   // OpenFeign to policy-service

    @Override
    public KpiSummaryDto calculateExecutiveKpis(LocalDate startDate, LocalDate endDate) {
        // 1. Fetch Aggregated Premiums Collected from premium-service
        BigDecimal premiumCollected = premiumClient.getTotalCollectionsBetween(startDate, endDate).getData();
        if (premiumCollected == null || premiumCollected.compareTo(BigDecimal.ZERO) == 0) {
            premiumCollected = BigDecimal.ONE; // Prevent division by zero
        }

        // 2. Fetch Aggregated Claims Settled from claim-service
        BigDecimal claimsPaid = claimClient.getTotalSettlementsBetween(startDate, endDate).getData();
        if (claimsPaid == null) claimsPaid = BigDecimal.ZERO;

        // 3. THE ACTUARIAL LOSS RATIO FORMULA:
        // Loss Ratio (%) = (Claims Paid / Premium Collected) * 100
        BigDecimal lossRatio = claimsPaid
                .divide(premiumCollected, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(2, RoundingMode.HALF_UP);

        // 4. THE COLLECTION EFFICIENCY FORMULA:
        // Collection Efficiency (%) = (Actual Collected / Total Scheduled Due) * 100
        BigDecimal scheduledDue = premiumClient.getTotalScheduledDueBetween(startDate, endDate).getData();
        BigDecimal collectionEfficiency = BigDecimal.ZERO;
        if (scheduledDue != null && scheduledDue.compareTo(BigDecimal.ZERO) > 0) {
            collectionEfficiency = premiumCollected
                    .divide(scheduledDue, 4, RoundingMode.HALF_UP)
                    .multiply(BigDecimal.valueOf(100))
                    .setScale(2, RoundingMode.HALF_UP);
        }

        log.info("KPIs Computed: Loss Ratio: {}%, Collection Efficiency: {}%", lossRatio, collectionEfficiency);
        return KpiSummaryDto.builder()
                .totalPremiumCollected(premiumCollected)
                .totalClaimsPaid(claimsPaid)
                .lossRatio(lossRatio)
                .collectionEfficiency(collectionEfficiency)
                .solvencyStatus(lossRatio.compareTo(BigDecimal.valueOf(85.0)) > 0 ? "WARNING: HIGH LOSS RATIO" : "HEALTHY")
                .build();
    }
}
```

---

## 4. Senior Developer Viva & Architecture Defense Cheat Sheet

| Question | Model Senior Developer Answer |
| :--- | :--- |
| **Q1: What is the Loss Ratio in Insurance?** | *"Loss Ratio is the fundamental metric of underwriting profitability: $(\text{Claims Paid} / \text{Premium Collected}) \times 100$. A loss ratio below 70% indicates healthy profitability; above 100% means the insurer is paying more in claims than it earns, threatening solvency."* |
| **Q2: Why use `analytics_snapshot` instead of querying live tables?** | *"Querying millions of policy, premium, and claim records in real-time would lock database rows and degrade transactional performance. Our service runs nightly background cron aggregation jobs to pre-calculate daily snapshots for instant dashboard retrieval."* |

---
