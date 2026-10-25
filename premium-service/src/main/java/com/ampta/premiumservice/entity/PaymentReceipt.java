package com.ampta.premiumservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "payment_receipts", indexes = {
        @Index(name = "idx_receipt_payment", columnList = "payment_id"),
        @Index(name = "idx_receipt_number", columnList = "receipt_number", unique = true)
})
public class PaymentReceipt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long receiptId;

    @Column(nullable = false)
    private Long paymentId;

    @Column(nullable = false, unique = true, length = 50)
    private String receiptNumber;

    private Long policyId;

    private Long customerId;

    private BigDecimal amountPaid;

    private LocalDateTime receiptDate;

    @Column(length = 500)
    private String receiptPath;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;
}
