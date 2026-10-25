package com.ampta.premiumservice.entity;

import com.ampta.premiumservice.entity.enums.PaymentMode;
import com.ampta.premiumservice.entity.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
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
@Entity
@Table(name = "premium_payments")
public class PremiumPayment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;
    private Long scheduleId;
    private Long policyId;
    private Long customerId;
    private BigDecimal amountPaid;

    @Enumerated(EnumType.STRING)
    private PaymentMode paymentMode;
    private String transactionReference;
    private PaymentStatus paymentStatus = PaymentStatus.INITIATED;
    private LocalDateTime paymentDate;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

}
