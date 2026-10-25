package com.ampta.premiumservice.entity;

import com.ampta.premiumservice.entity.enums.InstallmentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "premium_schedules",
        indexes = @Index(name = "idx_schedule_policy", columnList = "policy_id")
)
public class PremiumSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long scheduleId;
    private Long policyId;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal premiumAmount;
    private BigDecimal lateFee = BigDecimal.ZERO;
    private Integer gracePeriodDays = 30;

    @Enumerated(EnumType.STRING)
    private InstallmentStatus status = InstallmentStatus.PENDING;
    private LocalDate paidDate;

    @CreationTimestamp()
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
