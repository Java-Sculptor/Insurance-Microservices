package com.ampta.premiumservice.repository;

import com.ampta.premiumservice.entity.PaymentReceipt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PaymentReceiptRepository extends JpaRepository<PaymentReceipt, Long> {
    Optional<PaymentReceipt> findByPaymentId(Long paymentId);
    Optional<PaymentReceipt> findByReceiptNumber(String receiptNumber);
    boolean existsByPaymentId(Long paymentId);
}
