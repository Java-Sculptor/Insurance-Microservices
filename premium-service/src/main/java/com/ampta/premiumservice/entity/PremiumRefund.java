package com.ampta.premiumservice.entity;

import com.ampta.premiumservice.entity.enums.RefundStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "premium_refunds", indexes = {
        @Index(name = "idx_refund_payment", columnList = "payment_id")
})
public class PremiumRefund {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long refundId;

    @Column(nullable = false)
    private Long paymentId;

    private Long policyId;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal refundAmount;

    private LocalDateTime refundDate;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private RefundStatus refundStatus = RefundStatus.PENDING;

    private Long approvedBy;

    @Column(length = 1000)
    private String reason;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
}
