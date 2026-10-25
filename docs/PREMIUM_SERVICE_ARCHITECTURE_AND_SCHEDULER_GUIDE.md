# Enterprise Architecture & Implementation Guide: Premium Service, Schedulers, and Feign Clients

> **Target Audience**: Software Engineers and Backend Developers  
> **System Context**: Insurance Policy Administration System (PAS) — `premium-service`  
> **Version**: 1.0.0  

---

## Table of Contents
1. [Architectural Overview: Why Microservices & Premium Module?](#1-architectural-overview)
2. [Schedulers in Depth: The 'Why', 'When', and 'How'](#2-schedulers-in-depth)
   - 2.1 [Why Schedulers are Required in this Project](#21-why-schedulers-are-required-in-this-project)
   - 2.2 [Detailed Walkthrough of Each Scheduler in This Service](#22-detailed-walkthrough-of-each-scheduler-in-this-service)
   - 2.3 [Mental Model: How to Identify When ANY Project Needs a Scheduler](#23-mental-model-how-to-identify-when-any-project-needs-a-scheduler)
   - 2.4 [How to Implement Schedulers (In This & Other Tech Stacks)](#24-how-to-implement-schedulers)
   - 2.5 [Critical Production Gotcha: Distributed Schedulers (ShedLock)](#25-critical-production-gotcha-distributed-schedulers-shedlock)
3. [Inter-Service Communication: Feign Client, Load Balancers & `client/` Architecture](#3-inter-service-communication)
   - 3.1 [Why We Created the `client/` Folder](#31-why-we-created-the-client-folder)
   - 3.2 [Why and How `policy-service` is Called](#32-why-and-how-policy-service-is-called)
   - 3.3 [Declarative REST: Why OpenFeign Over RestTemplate / WebClient](#33-declarative-rest-why-openfeign-over-resttemplate--webclient)
   - 3.4 [What is a Load Balancer and Why is it Essential?](#34-what-is-a-load-balancer-and-why-is-it-essential)
   - 3.5 [Mental Model: When to Use Feign & Load Balancing in Any Architecture](#35-mental-model-when-to-use-feign--load-balancing-in-any-architecture)
   - 3.6 [Step-by-Step Guide: Implementing Feign + Load Balancer in Any Spring Boot App](#36-step-by-step-guide-implementing-feign--load-balancer-in-any-spring-boot-app)
4. [File-by-File Detailed Technical Walkthrough (All 46 Java Files)](#4-file-by-file-detailed-technical-walkthrough)
   - 4.1 [Application Entrypoint](#41-application-entrypoint)
   - 4.2 [Client Layer (`client/`)](#42-client-layer)
   - 4.3 [Scheduler Layer (`scheduler/`)](#43-scheduler-layer)
   - 4.4 [Controller Layer (`controller/`)](#44-controller-layer)
   - 4.5 [Service Layer (`service/` & `service/impl/`)](#45-service-layer)
   - 4.6 [Domain Entities (`entity/`)](#46-domain-entities)
   - 4.7 [Enumerations (`entity/enums/`)](#47-enumerations)
   - 4.8 [Data Transfer Objects (`dto/`)](#48-data-transfer-objects)
   - 4.9 [Persistence Repositories (`repository/`)](#49-persistence-repositories)
   - 4.10 [Exception Handling (`exception/`)](#410-exception-handling)
5. [Summary Checklist & Best Practices](#5-summary-checklist--best-practices)

---

## 1. Architectural Overview

In a monolithic application, scheduling a background thread, writing to shared tables, and querying a single database for both policies and payments is straightforward. However, an **Insurance Policy Administration System (PAS)** must scale to hundreds of thousands of active policies, millions of scheduled installments, and heavy concurrent payment spikes during premium due cycles.

To ensure high availability, compliance with **IRDAI (Insurance Regulatory and Development Authority)** guidelines, and independent scalability, the PAS system is decomposed into specialized microservices:
1. **`customer-service`**: Onboarding, KYC, contact records.
2. **`quote-service`**: Premium calculation engine, risk assessment, rider selection.
3. **`underwriting-service`**: Medical, financial, and manual underwriter approval.
4. **`policy-issuance-service`**: Generates policy contracts, policy numbers, and digital PDFs.
5. **`premium-service` (This Service)**: Installment schedule generation, payment collections, multi-mode verification, digital receipts, refund management, and automated background life-cycle maintenance.

---

## 2. Schedulers in Depth: The 'Why', 'When', and 'How'

### 2.1 Why Schedulers are Required in this Project

HTTP APIs operate strictly on a **Request-Response** model: a user clicks a button, a browser or mobile app sends an HTTP POST/GET, the backend processes it, and returns an HTTP response.

**The Problem**:
Time does not wait for a user HTTP request!
- What happens at midnight when a policyholder's grace period expires? No customer is clicking an "Expire My Grace Period" button!
- What happens 15 days before an installment is due? No customer calls the server to say "Please send me an SMS reminder today."
- What happens if a customer initiates a UPI payment at 11:58 PM, closes their browser during the bank redirect, and the bank callback is dropped?

Without background scheduling:
- Unpaid installments would remain `PENDING` forever.
- No late fees would ever be calculated.
- Reminders would never be dispatched.
- Stranded payments would leave policies in limbo.

A **Scheduler (or Cron Job)** is a proactive, autonomous worker that wakes up on a predetermined schedule, scans the database for records meeting time-dependent conditions, executes business rules, and updates system state without requiring human intervention.

---

### 2.2 Detailed Walkthrough of Each Scheduler in This Service

```
                  ┌────────────────────────────────────────┐
                  │          Spring Task Scheduler         │
                  └──────┬────────────┬────────────┬───────┘
                         │            │            │
            Every Night  │     Daily  │      Every │
             at 00:00 AM │   08:00 AM │     15 Min │
                         ▼            ▼            ▼
             ┌───────────────┐ ┌──────────────┐ ┌────────────────────┐
             │PremiumOverdue │ │PremiumReminder│ │PaymentReconcil.   │
             │   Scheduler   │ │  Scheduler   │ │     Scheduler      │
             └───────┬───────┘ └──────┬───────┘ └─────────┬──────────┘
                     │                │                   │
                     ▼                ▼                   ▼
            • Check Grace Period   • T-15 Days Nudge   • Find INITIATED
            • Mark OVERDUE         • T-7 Days Nudge    • Timeout after 15m
            • Calc Daily Late Fee  • Due Date Notice   • Prevent Deadlocks
                                   • T+3 Grace Notice
                                   • T+7 Final Warning
```

#### Scheduler 1: `PremiumOverdueScheduler` (Overdue Assessment & Dynamic Late Fee)
* **Frequency**: Daily at 00:00 (Midnight) — Cron: `0 0 0 * * ?`
* **Real-World Business Scenario**:
  A customer has a health insurance policy with a quarterly installment of ₹5,000 due on **October 1st**.
  - **Grace Period (FR-PRM-006)**: For quarterly policies, the grace period is **30 days**.
  - Between October 1st and October 31st, the customer is inside the grace period. The policy remains fully active and no late fee is applied.
  - On **November 1st at 00:00 AM**, `PremiumOverdueScheduler` triggers.
  - It detects that `today (Nov 1) > dueDate (Oct 1) + gracePeriod (30 days)`.
  - The scheduler automatically transitions the schedule status from `PENDING` to **`OVERDUE`**.
  - It calculates the late fee penalty:
    $$\text{Overdue Days} = 1$$
    $$\text{Late Fee} = 1 \text{ day} \times ₹10/\text{day} = ₹10$$
  - Every subsequent midnight that the payment remains unpaid, the scheduler increments the overdue days and recalculated late fee up to `maxPenaltyAmount` (₹5,000).

#### Scheduler 2: `PremiumReminderScheduler` (Automated Multi-Stage Reminders)
* **Frequency**: Daily at 08:00 AM — Cron: `0 0 8 * * ?`
* **Real-World Business Scenario (FR-PRM-008)**:
  Customers forget payment dates unless nudged systematically. The scheduler evaluates 5 distinct chronological milestones every morning:
  1. **T - 15 Days**: Friendly reminder ("Your installment of ₹3,717 is due on Nov 15th").
  2. **T - 7 Days**: Urgent reminder ("7 days left to maintain uninterrupted policy cover").
  3. **T (Due Date)**: Critical alert ("Your premium is due today. Pay now to avoid entering grace period").
  4. **T + 3 Days**: Grace Period notice ("Your premium is 3 days overdue. You are in your 30-day grace period").
  5. **T + 7 Days**: Escalation notice ("Final notice before late fee penalties start applying").
* **De-duplication Protection**: Before inserting a reminder into `premium_reminders`, the scheduler queries `existsByScheduleIdAndReminderStageAndCreatedAtAfter()` to guarantee a customer is never spammed twice for the same stage on the same day.

#### Scheduler 3: `PaymentReconciliationScheduler` (Orphaned Transaction Recovery — Optional / Gateway Mode)
* **Status**: **Retired in Option A (Direct/Simulated Mode)**; applicable when an asynchronous gateway (e.g. Razorpay) is integrated.
* **Frequency**: Every 15 minutes — Cron: `0 */15 * * * ?`
* **Real-World Business Scenario (FR-PRM-004)**:
  When an asynchronous third-party payment gateway is integrated, a policyholder selecting UPI might abandon their transaction during external bank redirection.
  - The payment record sits as `INITIATED`.
  - When active, this scheduler queries orphaned `INITIATED` records older than 15 minutes and transitions them to `FAILED`.
  - In our current direct/simulated payment mode (Option A), payments succeed synchronously upon hitting `POST /payments`, rendering this 15-minute polling unnecessary. Hence, it is disabled to optimize server resources.

---

### 2.3 Mental Model: How to Identify When ANY Project Needs a Scheduler

As a developer designing any system, review your requirements and ask these 5 diagnostic questions:

```
┌─────────────────────────────────────────────────────────────────────────┐
│                     SCHEDULER DECISION HEURISTIC                        │
├─────────────────────────────────────────────────────────────────────────┤
│ 1. Time-Triggered State Transition?                                     │
│    Does a database record need to change status because of date/time?   │
│    (e.g., Trial Expired, Cart Abandoned, Policy Overdue, Subscription)  │
│    👉 YES = Scheduler Required                                          │
│                                                                         │
│ 2. Proactive Outbound Communication?                                    │
│    Do emails/SMS/push notifications need to go out at a specific time?  │
│    (e.g., Birthday discounts, payment reminders, flight check-in)       │
│    👉 YES = Scheduler Required                                          │
│                                                                         │
│ 3. Data Cleanup & Housekeeping?                                         │
│    Do temporary files, expired JWT blacklist tokens, or soft-deleted    │
│    records need purging after X days?                                   │
│    👉 YES = Scheduler Required                                          │
│                                                                         │
│ 4. Batch Financial Reconciliation?                                      │
│    Does the app need to balance daily accounts, tally sales, or compute │
│    nightly vendor settlements?                                          │
│    👉 YES = Scheduler Required                                          │
│                                                                         │
│ 5. Third-Party Polling / Sync?                                          │
│    Do you need to ingest bank statements or stock prices every morning? │
│    👉 YES = Scheduler Required                                          │
└─────────────────────────────────────────────────────────────────────────┘
```

---

### 2.4 How to Implement Schedulers

#### In Spring Boot (Java):
1. **Enable Scheduling**: Add `@EnableScheduling` to your main class:
   ```java
   @SpringBootApplication
   @EnableScheduling
   public class Application { ... }
   ```
2. **Create a Managed Component**: Mark your class with `@Component`:
   ```java
   @Component
   @RequiredArgsConstructor
   @Slf4j
   public class MyJobScheduler {
       private final MyService service;

       @Scheduled(cron = "0 0 0 * * ?") // Every day at midnight
       public void executeTask() {
           service.doBatchWork();
       }
   }
   ```
3. **Cron Expression Cheat-Sheet (Spring 6 fields)**:
   - `second, minute, hour, day-of-month, month, day-of-week`
   - `"0 0 0 * * ?"` = Daily at midnight (00:00:00)
   - `"0 0 8 * * ?"` = Daily at 08:00 AM
   - `"0 */15 * * * ?"` = Every 15 minutes
   - `"0 0 12 1 * ?"` = 12:00 PM on the 1st day of every month

#### In Other Tech Stacks:
* **Node.js**:
  - In-process: Use `node-cron` or `cron`.
  - Distributed / Enterprise: Use **BullMQ** or **Agenda** with Redis.
* **Python**:
  - In-process: Use `APScheduler`.
  - Distributed: Use **Celery Beat** with RabbitMQ/Redis.
* **Go**:
  - Use `robfig/cron/v3`.

---

### 2.5 Critical Production Gotcha: Distributed Schedulers (ShedLock)

> [!WARNING]
> In production, microservices are deployed with **multiple replicas (instances)** behind a container orchestrator (e.g., Kubernetes with 3 pods).
> If you have 3 running instances of `premium-service`, standard `@Scheduled` will run **3 times concurrently** on all 3 pods at midnight! This can cause customers to receive 3 duplicate emails or charge multiple late fees.

**The Solution**: Distributed Locking with **ShedLock** or **Quartz**:
```java
@Scheduled(cron = "0 0 0 * * ?")
@SchedulerLock(name = "PremiumOverdueTask", lockAtLeastFor = "PT5M", lockAtMostFor = "PT14M")
public void runDailyOverdueAssessment() { ... }
```
ShedLock stores a lock record in the database or Redis so that only **one instance** acquires the lock and runs the job while the others gracefully skip.

---

## 3. Inter-Service Communication: Feign Client, Load Balancers & `client/` Architecture

### 3.1 Why We Created the `client/` Folder

In Clean Architecture and Domain-Driven Design (DDD), external system dependencies must be encapsulated behind a **Boundary/Port**. 

If controllers or services throughout `premium-service` had ad-hoc HTTP calls with raw URLs like `http://policy-service:8082/api/v1/policies/` scattered across the code:
1. Any change in the Policy Service's REST path would break multiple files.
2. Error handling and fallbacks would be duplicated.
3. Unit testing would become cumbersome.

By isolating all external communication inside the `client/` package:
* `PolicyClient.java` acts as the **declarative contract**.
* `PolicyClientFallback.java` acts as the **fault tolerance buffer**.
* All internal business services (`PremiumPaymentServiceImpl`) only depend on the clean Java interface, completely oblivious to network protocols, hostnames, or JSON serialization.

---

### 3.2 Why and How `policy-service` is Called

```
┌────────────────────────┐                   ┌────────────────────────┐
│    premium-service     │                   │     policy-service     │
│ ┌────────────────────┐ │                   │ ┌────────────────────┐ │
│ │ PremiumPaymentImpl │ │                   │ │ PolicyController   │ │
│ └─────────┬──────────┘ │                   │ └─────────▲──────────┘ │
│           │            │                   │           │            │
│           ▼            │                   │           │            │
│ ┌────────────────────┐ │  HTTP GET         │           │            │
│ │   PolicyClient     ├─┼───────────────────┼───────────┘            │
│ └─────────┬──────────┘ │  /api/v1/policies │                        │
│           │            │  /{id}            │                        │
│ (If Down) ▼            │                   │                        │
│ ┌────────────────────┐ │                   │                        │
│ │PolicyClientFallback│ │                   │                        │
│ └────────────────────┘ │                   │                        │
└────────────────────────┘                   └────────────────────────┘
```

#### Why We Call `policy-service`:
Before accepting money for an insurance installment, the business rule mandates:
1. **Validation of Policy Existence**: Verify that the policy ID attached to the installment actually exists in the core PAS database.
2. **Policy Status Enforcement**: A customer cannot pay a premium for a policy that is `CANCELLED`, `LAPSED`, or already `CLOSED`. Payments are strictly restricted to policies in `ACTIVE`, `DRAFT`, or `PENDING_PAYMENT` status.
3. **Customer Identification**: If the payment request omitted the customer ID, it is dynamically retrieved from the master policy record.

#### How It Works Under the Hood:
1. `PremiumPaymentServiceImpl` calls `policyClient.getPolicy(policyId)`.
2. OpenFeign intercepts this method call, queries the service registry/load balancer for an available IP of `policy-service`, creates an HTTP GET request to `/api/v1/policies/{policyId}`, and parses the JSON response into a [PolicyDto](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/PolicyDto.java).
3. If `policy-service` is unreachable or times out, OpenFeign triggers the **Circuit Breaker Fallback** ([PolicyClientFallback.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/client/PolicyClientFallback.java)), preventing the entire `premium-service` thread from crashing.

---

### 3.3 Declarative REST: Why OpenFeign Over RestTemplate / WebClient

Consider making an HTTP call using standard `RestTemplate`:
```java
// The Old, Fragile Way:
String url = "http://policy-service/api/v1/policies/" + id;
HttpHeaders headers = new HttpHeaders();
headers.set("Authorization", "Bearer " + token);
HttpEntity<String> entity = new HttpEntity<>(headers);
ResponseEntity<PolicyDto> response = restTemplate.exchange(url, HttpMethod.GET, entity, PolicyDto.class);
PolicyDto dto = response.getBody();
```
*Notice how much boilerplate code is needed: URL string concatenation, error code handling, headers, manual mapping.*

Now look at **OpenFeign**:
```java
@FeignClient(name = "policy-service", fallback = PolicyClientFallback.class)
public interface PolicyClient {
    @GetMapping("/api/v1/policies/{id}")
    PolicyDto getPolicy(@PathVariable("id") Long id);
}
```
You write a simple Java interface, annotate it with standard Spring MVC annotations, and Spring Cloud OpenFeign **generates the entire HTTP client implementation dynamically at runtime**.

---

### 3.4 What is a Load Balancer and Why is it Essential?

In production, you never run a single instance of a microservice. You run multiple instances:
* `policy-service Instance 1`: `192.168.1.10:8082`
* `policy-service Instance 2`: `192.168.1.11:8082`
* `policy-service Instance 3`: `192.168.1.12:8082`

**Server-Side Load Balancer (e.g., NGINX / AWS ALB)**:
The client calls `http://my-alb/policies`. The ALB receives the request and forwards it to one of the servers. This introduces an extra network hop and a single point of failure.

**Client-Side Load Balancer (Spring Cloud LoadBalancer)**:
When `premium-service` has OpenFeign configured with service discovery:
1. `premium-service` receives the list of all live IP addresses for `policy-service` directly from the Service Registry (Eureka/Consul).
2. The client-side load balancer executes an algorithm (e.g., **Round Robin** or **Least Connections**) locally in memory.
3. The HTTP request is dispatched **directly** to `192.168.1.11:8082` without intermediate hardware hops.

---

### 3.5 Mental Model: When to Use Feign & Load Balancing in Any Architecture

```
┌────────────────────────────────────────────────────────────────────────┐
│                   INTER-SERVICE COMMUNICATION MATRIX                   │
├───────────────────────┬────────────────────────────────────────────────┤
│ Situation             │ Recommended Pattern                            │
├───────────────────────┼────────────────────────────────────────────────┤
│ Synchronous Read      │ Feign Client + Client-side Load Balancing      │
│ (Need data right now) │ (e.g. Get Customer details, Check Policy status)│
├───────────────────────┼────────────────────────────────────────────────┤
│ Asynchronous Action   │ Message Broker / Event Bus                     │
│ (Fire and Forget)     │ (e.g. RabbitMQ/Kafka for "Send Welcome Email") │
├───────────────────────┼────────────────────────────────────────────────┤
│ High-Throughput Stream│ Reactive WebClient or gRPC                     │
│ (Real-time telemetry) │ (e.g. IoT GPS vehicle tracking)                │
└───────────────────────┴────────────────────────────────────────────────┘
```

---

### 3.6 Step-by-Step Guide: Implementing Feign + Load Balancer in Any Spring Boot App

1. **Add Dependencies (`pom.xml`)**:
   ```xml
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-openfeign</artifactId>
   </dependency>
   <dependency>
       <groupId>org.springframework.cloud</groupId>
       <artifactId>spring-cloud-starter-loadbalancer</artifactId>
   </dependency>
   ```
2. **Enable Feign in Application Class**:
   ```java
   @SpringBootApplication
   @EnableFeignClients
   public class Application { ... }
   ```
3. **Declare the Feign Interface**:
   ```java
   @FeignClient(name = "service-name", path = "/api/v1")
   public interface UserClient {
       @GetMapping("/users/{id}")
       UserDto getUserById(@PathVariable("id") Long id);
   }
   ```
4. **Configure Eureka or Static URL (in `application.properties`)**:
   ```properties
   eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
   ```

---

## 4. File-by-File Detailed Technical Walkthrough (All 46 Java Files)

Here is the complete architectural reference for every file inside [src/main/java/com/ampta/premiumservice](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice).

---

### 4.1 Application Entrypoint

#### 1. [PremiumServiceApplication.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/PremiumServiceApplication.java)
* **Layer**: Core Configuration / Bootstrapper
* **Role**: The main bootstrap class that starts the Spring Boot runtime.
* **Key Mechanics**:
  - `@SpringBootApplication`: Configures component scanning, auto-configuration, and property loading.
  - `@EnableScheduling`: Activates Spring’s background task executor infrastructure so that methods annotated with `@Scheduled` are automatically picked up and run.
  - `@EnableFeignClients`: Scans the classpath for interfaces marked with `@FeignClient` and dynamically builds the HTTP proxy implementations.

---

### 4.2 Client Layer (`client/`)

#### 2. [PolicyClient.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/client/PolicyClient.java)
* **Layer**: Feign Declarative HTTP Client
* **Role**: The typed API contract for communicating with `policy-service`.
* **Key Mechanics**:
  - `@FeignClient(name = "policy-service", fallback = PolicyClientFallback.class)`: Directs Spring Cloud to discover instances of `policy-service` via load balancing and defines the graceful fallback bean if the target service is unreachable.
  - `getPolicy(@PathVariable("id") Long id)`: Maps to `GET /api/v1/policies/{id}`.

#### 3. [PolicyClientFallback.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/client/PolicyClientFallback.java)
* **Layer**: Fault Tolerance & Resilience Fallback
* **Role**: Implements `PolicyClient` to provide fallback data when `policy-service` is down or timing out.
* **Key Mechanics**:
  - Returns a default mock `PolicyDto` with active status, preventing payment gateway failures during transient network splits in development or staging.

---

### 4.3 Scheduler Layer (`scheduler/`)

#### 4. [PremiumOverdueScheduler.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/scheduler/PremiumOverdueScheduler.java)
* **Layer**: Background Job
* **Role**: Daily midnight batch job that handles policy grace period expirations and late fee computations.
* **Key Mechanics**:
  - `@Scheduled(cron = "${premium.scheduler.overdue.cron:0 0 0 * * ?}")`: Runs at 00:00 every night.
  - Delegates execution to `premiumScheduleService.assessOverdueAndCalculateLateFees()`.

#### 5. [PremiumReminderScheduler.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/scheduler/PremiumReminderScheduler.java)
* **Layer**: Background Job
* **Role**: Daily morning job that evaluates 5 chronological installment milestones (T-15, T-7, Due Date, T+3, T+7) and records notification dispatches.
* **Key Mechanics**:
  - `@Scheduled(cron = "${premium.scheduler.reminder.cron:0 0 8 * * ?}")`: Runs at 8 AM.
  - Checks database de-duplication before saving `PremiumReminder` records.

#### 6. [PaymentReconciliationScheduler.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/scheduler/PaymentReconciliationScheduler.java)
* **Layer**: Background Job
* **Role**: Automated 15-minute cleaner that catches abandoned or dropped payment gateway transactions.
* **Key Mechanics**:
  - `@Scheduled(cron = "${premium.scheduler.reconciliation.cron:0 */15 * * * ?}")`: Runs every 15 minutes.
  - Invokes `premiumPaymentService.reconcilePendingPayments()` to transition orphaned `INITIATED` records to `FAILED`.

---

### 4.4 Controller Layer (`controller/`)

#### 7. [PremiumController.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/controller/PremiumController.java)
* **Layer**: REST API Presentation
* **Role**: Primary ingress controller exposing endpoints for installment schedules, payments, receipts, and verification.
* **Key Endpoints**:
  - `POST /api/v1/premium-schedules`: Generate installments for newly issued policies.
  - `GET /api/v1/premium-schedules/{policyId}`: Retrieve schedules for a policy.
  - `PUT /api/v1/premium-schedules/{scheduleId}`: Update status or waive late fee.
  - `POST /api/v1/payments`: Process payment for an installment.
  - `GET /api/v1/payments/{paymentId}`: Get single payment transaction.
  - `GET /api/v1/payments/history/{policyId}`: Retrieve all payments for a policy.
  - `POST /api/v1/payments/verify`: Gateway callback verification.
  - `GET /api/v1/payments/{paymentId}/receipt`: Retrieve digital receipt.
  - `POST /api/v1/payments/{paymentId}/receipt/regenerate`: Re-issue receipt metadata.

#### 8. [RefundController.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/controller/RefundController.java)
* **Layer**: REST API Presentation
* **Role**: Exposes endpoints for policy cancellation refunds and financial approval workflows.
* **Key Endpoints**:
  - `POST /api/v1/refunds`: Initiate a refund request.
  - `PUT /api/v1/refunds/{refundId}/approve`: Approve or reject a refund.
  - `GET /api/v1/refunds/{refundId}`: Query refund details.
  - `GET /api/v1/refunds/policy/{policyId}`: View all refunds for a policy.

---

### 4.5 Service Layer (`service/` & `service/impl/`)

#### 9. [PremiumScheduleService.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/service/PremiumScheduleService.java)
* **Layer**: Business Logic Interface
* **Role**: Contract for schedule generation, installment lookups, overdue calculations, and date-based queries.

#### 10. [PremiumScheduleServiceImpl.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/service/impl/PremiumScheduleServiceImpl.java)
* **Layer**: Business Logic Implementation
* **Role**: Implements schedule mathematics and late-fee algorithms.
* **Key Logic**:
  - `generateSchedule`: Divides `finalPremium` across payment frequencies (`MONTHLY`: 12, `QUARTERLY`: 4, `HALF_YEARLY`: 2, `YEARLY`: 1, `SINGLE`: 1). Sets installment 1 to policy start date, assigns grace period (15 or 30 days), and adjusts rounding pennies on the final installment.
  - `assessOverdueAndCalculateLateFees`: Scans unpaid installments past due date + grace period, computes penalties, and transitions them to `OVERDUE`.

#### 11. [PremiumPaymentService.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/service/PremiumPaymentService.java)
* **Layer**: Business Logic Interface
* **Role**: Contract for payments, receipts, gateway verifications, and reconciliation.

#### 12. [PremiumPaymentServiceImpl.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/service/impl/PremiumPaymentServiceImpl.java)
* **Layer**: Business Logic Implementation
* **Role**: Handles payment execution and receipt generation.
* **Key Logic**:
  - `payPremium`: Validates unique `transactionReference` to stop duplicate billing, validates active policy state via `PolicyClient`, saves `PremiumPayment`, sets installment to `PAID` or `PARTIAL_PAID`, and automatically creates a `PaymentReceipt`.
  - `reconcilePendingPayments`: Times out stranded `INITIATED` transactions.

#### 13. [PremiumRefundService.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/service/PremiumRefundService.java)
* **Layer**: Business Logic Interface
* **Role**: Contract for refund initiation, approvals, and queries.

#### 14. [PremiumRefundServiceImpl.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/service/impl/PremiumRefundServiceImpl.java)
* **Layer**: Business Logic Implementation
* **Role**: Validates that refund amount does not exceed original payment amount, tracks authorization by `approvedBy`, and transitions `PremiumPayment` status to `REFUNDED`.

---

### 4.6 Domain Entities (`entity/`)

#### 15. [PremiumSchedule.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/PremiumSchedule.java)
* **Layer**: JPA Entity (`premium_schedules`) — BRD `INS036`
* **Role**: Represents an installment row. Fields: `scheduleId`, `policyId`, `installmentNo`, `dueDate`, `premiumAmount`, `lateFee`, `gracePeriodDays`, `status`, `paidDate`.

#### 16. [PremiumPayment.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/PremiumPayment.java)
* **Layer**: JPA Entity (`premium_payments`) — BRD `INS037`
* **Role**: Represents a financial transaction. Fields: `paymentId`, `scheduleId`, `policyId`, `customerId`, `amountPaid`, `paymentMode`, `transactionReference`, `paymentStatus`, `paymentDate`.

#### 17. [PaymentReceipt.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/PaymentReceipt.java)
* **Layer**: JPA Entity (`payment_receipts`) — BRD `INS038`
* **Role**: Stores generated digital receipts. Fields: `receiptId`, `paymentId`, `receiptNumber`, `policyId`, `customerId`, `amountPaid`, `receiptDate`, `receiptPath`.

#### 18. [PremiumReminder.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/PremiumReminder.java)
* **Layer**: JPA Entity (`premium_reminders`) — BRD `INS039`
* **Role**: Audit trail of outgoing reminders. Fields: `reminderId`, `scheduleId`, `policyId`, `reminderType`, `reminderDate`, `deliveryStatus`, `reminderStage`, `remarks`.

#### 19. [PremiumRefund.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/PremiumRefund.java)
* **Layer**: JPA Entity (`premium_refunds`) — BRD `INS040`
* **Role**: Records refunds for cancelled or endorsed policies. Fields: `refundId`, `paymentId`, `policyId`, `refundAmount`, `refundDate`, `refundStatus`, `approvedBy`, `reason`.

---

### 4.7 Enumerations (`entity/enums/`)

#### 20. [InstallmentStatus.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/enums/InstallmentStatus.java)
* Values: `PENDING`, `PAID`, `PARTIAL_PAID`, `OVERDUE`, `CANCELLED`.
* Tracks lifecycle states of an installment schedule.

#### 21. [PaymentStatus.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/enums/PaymentStatus.java)
* Values: `INITIATED`, `SUCCESS`, `FAILED`, `PENDING`, `REFUNDED`, `CANCELLED`.
* Tracks transaction states from gateway creation to settlement or refund.

#### 22. [PaymentMode.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/enums/PaymentMode.java)
* Values: `UPI`, `CREDIT_CARD`, `DEBIT_CARD`, `NET_BANKING`, `CASH`, `CHEQUE`, `DD`, `WALLET`.
* Supports all online and offline payment avenues mandated by BRD Section 8.

#### 23. [DeliveryStatus.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/enums/DeliveryStatus.java)
* Values: `PENDING`, `SENT`, `FAILED`.
* Tracks outbound notification status for reminders.

#### 24. [RefundStatus.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/enums/RefundStatus.java)
* Values: `PENDING`, `APPROVED`, `COMPLETED`, `REJECTED`.
* Tracks multi-stage approval workflow for refunds.

#### 25. [ReminderType.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/entity/enums/ReminderType.java)
* Values: `SMS`, `EMAIL`, `WHATSAPP`, `PUSH_NOTIFICATION`.
* Communication channels supported for customer reminders.

---

### 4.8 Data Transfer Objects (`dto/`)

#### 26. [ApiResponse.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/ApiResponse.java)
* Standard response wrapper matching BRD Section 1.13 (`success`, `message`, `data`, `timestamp`).

#### 27. [ScheduleRequestDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/ScheduleRequestDto.java)
* Payload sent by `policy-issuance-service` containing `policyId`, `policyTermMonths`, `premiumFrequency`, `startDate`, and `finalPremium`.

#### 28. [ScheduleResponseDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/ScheduleResponseDto.java)
* Response containing installment breakdown, due dates, late fees, grace period, and total payable amounts.

#### 29. [ScheduleUpdateDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/ScheduleUpdateDto.java)
* Payload for manual admin modifications (e.g., status changes or fee adjustments).

#### 30. [PaymentRequestDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/PaymentRequestDto.java)
* Inbound payment submission payload containing `scheduleId`, `amountPaid`, `paymentMode`, and `transactionReference`.

#### 31. [PaymentResponseDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/PaymentResponseDto.java)
* Outbound payment confirmation response.

#### 32. [PaymentVerificationDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/PaymentVerificationDto.java)
* Gateway callback payload for asynchronous transaction confirmation.

#### 33. [ReceiptResponseDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/ReceiptResponseDto.java)
* Encapsulates all receipt details: `receiptNumber`, `policyNumber`, `customerName`, `amountPaid`, and download path.

#### 34. [RefundRequestDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/RefundRequestDto.java)
* Inbound payload for initiating a refund.

#### 35. [RefundApprovalDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/RefundApprovalDto.java)
* Payload used by Finance Officers to approve or reject a refund.

#### 36. [RefundResponseDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/RefundResponseDto.java)
* Response returning refund status and execution timestamp.

#### 37. [PolicyDto.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/dto/PolicyDto.java)
* Data model mirroring the external `policy-service` payload used during inter-service validation.

---

### 4.9 Persistence Repositories (`repository/`)

#### 38. [PremiumScheduleRepository.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/repository/PremiumScheduleRepository.java)
* Spring Data JPA repository for `PremiumSchedule`. Contains queries:
  - `findByPolicyIdOrderByInstallmentNoAsc`
  - `findByStatusInAndDueDateBefore` (used by Overdue Scheduler)
  - `findByStatusInAndDueDate` (used by Reminder Scheduler)

#### 39. [PremiumPaymentRepository.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/repository/PremiumPaymentRepository.java)
* Spring Data JPA repository for `PremiumPayment`. Contains queries:
  - `existsByTransactionReference` (duplicate payment check)
  - `findByTransactionReference` (reconciliation / callback lookups)
  - `findByPaymentStatusAndCreatedAtBefore` (used by Reconciliation Scheduler)

#### 40. [PaymentReceiptRepository.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/repository/PaymentReceiptRepository.java)
* Spring Data JPA repository for `PaymentReceipt`. Handles receipt lookups by `paymentId` or unique `receiptNumber`.

#### 41. [PremiumReminderRepository.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/repository/PremiumReminderRepository.java)
* Spring Data JPA repository for `PremiumReminder`. Queries reminder history and prevents redundant notifications via `existsByScheduleIdAndReminderStageAndCreatedAtAfter`.

#### 42. [PremiumRefundRepository.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/repository/PremiumRefundRepository.java)
* Spring Data JPA repository for `PremiumRefund`. Queries refunds by `policyId` and `paymentId`.

---

### 4.10 Exception Handling (`exception/`)

#### 43. [BusinessException.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/exception/BusinessException.java)
* Thrown when business logic constraints are violated (e.g., paying an already paid installment, paying on a cancelled policy, or requesting a refund greater than the amount paid).

#### 44. [DuplicatePaymentException.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/exception/DuplicatePaymentException.java)
* Thrown when an incoming payment attempts to reuse an existing `transactionReference`.

#### 45. [ResourceNotFoundException.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/exception/ResourceNotFoundException.java)
* Thrown when a queried entity (Schedule, Payment, Receipt, Refund) does not exist in the database.

#### 46. [GlobalExceptionHandler.java](file:///Users/apple/Workspace/team%2011/Insurance-Microservices/premium-service/src/main/java/com/ampta/premiumservice/exception/GlobalExceptionHandler.java)
* Centralized `@RestControllerAdvice` converting all thrown exceptions into standardized HTTP error responses (`400 Bad Request`, `404 Not Found`, `409 Conflict`, `422 Validation Error`).

---

## 5. Summary Checklist & Best Practices

```
                                    ENTERPRISE ARCHITECTURE SUMMARY
┌──────────────────────────────────────┬──────────────────────────────────────────────────────────────────┐
│ Feature                              │ Implementation Detail                                            │
├──────────────────────────────────────┼──────────────────────────────────────────────────────────────────┤
│ Core Engine                          │ Spring Boot 4.x, Java 25, Spring Data JPA, Hibernate, MySQL      │
│ Inter-Service Communication          │ Declarative OpenFeign Client with resilient Fallback             │
│ Service Discovery & Load Balancing   │ Client-side Spring Cloud LoadBalancer                            │
│ Installment Scheduling               │ Multi-frequency calculations with Penny-rounding protection      │
│ Grace Period Management              │ 15 days (Monthly), 30 days (Quarterly/Half-Yearly/Yearly)        │
│ Automated Schedulers                 │ 1. Daily Overdue & Late Fee (00:00 AM)                           │
│                                      │ 2. Multi-Stage Reminder (08:00 AM)                               │
│                                      │ 3. Payment Reconciliation (Every 15 min)                         │
│ Digital Receipts                     │ Auto-generation upon successful transaction (`REC-YYYY-XXXXXX`)  │
│ Refund Management                    │ Validation, initiation, and multi-level approval flow            │
└──────────────────────────────────────┴──────────────────────────────────────────────────────────────────┘
```
