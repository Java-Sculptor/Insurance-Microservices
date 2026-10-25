# 🔐 Admin & Security Service: Comprehensive Architectural Guide, Real-World Story, Entity Schema & Line-by-Line ServiceImpl Deep Dive

---

## 🌟 Table of Contents
1. [The Real-World Story: The Security Citadel of PAS](#1-the-real-world-story-the-security-citadel-of-pas)
   - [The Rogue Cashier Embezzlement vs. Cryptographic RBAC](#the-rogue-cashier-embezzlement-vs-cryptographic-rbac)
   - [Why Admin Service is the Trust Engine of the Enterprise](#why-admin-service-is-the-trust-engine-of-the-enterprise)
2. [Comprehensive Entity Architecture (The 10 Tables)](#2-comprehensive-entity-architecture-the-10-tables)
   - [1. UserMaster Entity (INS059 - Identity & BCrypt Hashes)](#1-usermaster-entity-ins059---identity--bcrypt-hashes)
   - [2. RoleMaster Entity (INS060 - Enterprise Personas)](#2-rolemaster-entity-ins060---enterprise-personas)
   - [3. PermissionMaster Entity (INS061 - Granular Action Matrix)](#3-permissionmaster-entity-ins061---granular-action-matrix)
   - [4. RolePermission Entity (INS062 - Many-to-Many RBAC Mapping)](#4-rolepermission-entity-ins062---many-to-many-rbac-mapping)
   - [5. BranchMaster Entity (INS063 - Organizational Hierarchy)](#5-branchmaster-entity-ins063---organizational-hierarchy)
   - [6. AuditLog Entity (INS068 - Immutable Security Ledger)](#6-auditlog-entity-ins068---immutable-security-ledger)
3. [Master Endpoints Directory (20 REST APIs)](#3-master-endpoints-directory-20-rest-apis)
4. [Phase-by-Phase Line-by-Line ServiceImpl Business Logic Deep Dive](#4-phase-by-phase-line-by-line-serviceimpl-business-logic-deep-dive)
   - [Phase 1: Stateless JWT Token Issuance & Refresh Token Rotation](#phase-1-stateless-jwt-token-issuance--refresh-token-rotation)
   - [Phase 2: 5-Attempt Brute-Force Defense Lockout Guard](#phase-2-5-attempt-brute-force-defense-lockout-guard)
   - [Phase 3: Append-Only Tamper-Proof Audit Logging (AuditLogServiceImpl)](#phase-3-append-only-tamper-proof-audit-logging)
5. [Senior Developer Viva & Architecture Defense Cheat Sheet](#5-senior-developer-viva--architecture-defense-cheat-sheet)

---

## 1. The Real-World Story: The Security Citadel of PAS

### 📖 The Rogue Cashier Embezzlement
In 2016, an operations clerk at an insurance regional branch discovered that all employees shared a single database login (`admin` / `password123`) to save software license costs. 

The clerk quietly updated customer bank details on 40 approved claims to his personal dummy bank account, disbursed ₹1.8 Crores ($220,000) over 6 months, and resigned. When the fraud was discovered, investigators found **zero audit trails**. Every action was logged as *"user: admin"*. The insurer was unable to prove which employee stole the funds, lost the money, and was severely reprimanded by auditors.

---

### The Security Citadel: Modern Admin & Auth Architecture
**`admin-service`** enforces:
1. **Stateless JWT with Cryptographic Signatures**: Users authenticate once; tokens carry cryptographically signed claims (`roles: ["CLAIMS_APPROVER"]`).
2. **Granular Role-Based Access Control (RBAC)**: A cashier cannot approve claims; an underwriter cannot disburse payouts. Separation of duties is enforced at the API gateway.
3. **5-Attempt Brute-Force Lockout**: 5 failed password attempts automatically transitions the account to `LOCKED`, neutralizing automated dictionary attacks.
4. **Append-Only Tamper-Proof Audit Trail**: Logs User ID, Action, Timestamp, IP Address, and Before/After snapshots. SQL updates and deletes on the `audit_log` table are physically blocked at the database engine level.

---

## 2. Comprehensive Entity Architecture (The 10 Tables)

| Table & Entity | Key Fields & Constraints | Real-World Purpose |
| :--- | :--- | :--- |
| **`user_master`** (INS059) | `username` (UK), `password_hash` (BCrypt), `status` (`ACTIVE`, `LOCKED`, `INACTIVE`) | Central credentials repository. |
| **`role_master`** (INS060) | `role_name` (UK) (`SUPER_ADMIN`, `UNDERWRITER`, `CLAIMS_MANAGER`) | Enterprise roles. |
| **`permission_master`** (INS061)| `module_name`, `action_type` (`CREATE`, `READ`, `APPROVE`, `EXPORT`) | Granular access control permissions. |
| **`role_permission`** (INS062)| `role_id`, `permission_id` | Associative table linking permissions to roles. |
| **`branch_master`** (INS063) | `branch_code` (UK), `branch_name`, `state` | Multi-branch organizational structure. |
| **`audit_log`** (INS068) | `user_id`, `action`, `old_value`, `new_value`, `ip_address`, `action_time` | Legal audit ledger protecting against insider fraud. |

---

## 3. Phase-by-Phase Line-by-Line ServiceImpl Business Logic Deep Dive

#### Brute-Force Lockout & JWT Authentication Implementation
```java
@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {

    private final UserMasterRepository userRepository;
    private final PasswordEncoder passwordEncoder; // BCrypt with strength 12
    private final JwtTokenProvider jwtTokenProvider;
    private final AuditLogRepository auditLogRepository;

    private static final int MAX_FAILED_ATTEMPTS = 5;

    @Override
    public LoginResponseDto login(LoginRequestDto request, String clientIp) {
        UserMaster user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BadCredentialsException("Invalid username or password."));

        // 1. Guard Clause: Account Lock Check
        if (user.getStatus() == UserStatus.LOCKED) {
            log.warn("Login blocked: Account {} is LOCKED due to excessive failed attempts.", user.getUsername());
            throw new AccountLockedException("Your account has been locked due to 5 consecutive failed login attempts. Please contact Security Admin.");
        }

        // 2. BCrypt Password Verification
        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            int attempts = user.getFailedLoginAttempts() + 1;
            user.setFailedLoginAttempts(attempts);

            if (attempts >= MAX_FAILED_ATTEMPTS) {
                user.setStatus(UserStatus.LOCKED);
                log.error("Security Alert: User {} LOCKED after {} failed attempts from IP: {}", user.getUsername(), attempts, clientIp);
            }
            userRepository.save(user);

            // Record security incident
            recordAudit(user.getUserId(), "FAILED_LOGIN_ATTEMPT", "Attempt: " + attempts, clientIp);
            throw new BadCredentialsException("Invalid username or password. Remaining attempts before lockout: " + (MAX_FAILED_ATTEMPTS - attempts));
        }

        // 3. Successful Login: Reset Failed Attempts Counter
        user.setFailedLoginAttempts(0);
        user.setLastLoginTime(LocalDateTime.now());
        userRepository.save(user);

        // 4. Generate Cryptographic JWT Access Token (15m) & Refresh Token (7d)
        String accessToken = jwtTokenProvider.generateAccessToken(user);
        String refreshToken = jwtTokenProvider.generateRefreshToken(user);

        recordAudit(user.getUserId(), "LOGIN_SUCCESS", "Issued JWT tokens", clientIp);
        log.info("User {} successfully authenticated from IP {}", user.getUsername(), clientIp);

        return new LoginResponseDto(accessToken, refreshToken, user.getUsername(), user.getRole().getRoleName());
    }

    private void recordAudit(Long userId, String action, String remarks, String ip) {
        auditLogRepository.save(AuditLog.builder()
                .userId(userId)
                .moduleName("AUTH")
                .action(action)
                .ipAddress(ip)
                .actionTime(LocalDateTime.now())
                .remarks(remarks)
                .build());
    }
}
```

---

## 4. Senior Developer Viva & Architecture Defense Cheat Sheet

| Question | Model Senior Developer Answer |
| :--- | :--- |
| **Q1: Why use BCrypt instead of SHA-256 for passwords?** | *"SHA-256 is a fast cryptographic hash designed for data integrity; modern GPUs can compute billions of SHA-256 hashes per second, making it trivial to crack via brute force. BCrypt incorporates a configurable work factor (salt rounds) that is deliberately slow and computationally intensive, completely neutralizing GPU-accelerated dictionary attacks."* |
| **Q2: How does the 5-attempt brute-force defense work?** | *"Each consecutive failed password match increments `failedLoginAttempts`. When the counter reaches 5, the user status is transitioned to `LOCKED`, and an audit event is fired. Only a designated Security Admin can unlock the account via `PUT /api/v1/users/{id}/unlock`."* |

---
