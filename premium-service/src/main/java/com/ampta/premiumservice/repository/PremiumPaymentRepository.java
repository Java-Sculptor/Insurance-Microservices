package com.ampta.premiumservice.repository;

import com.ampta.premiumservice.entity.PremiumPayment;
import com.ampta.premiumservice.entity.enums.PaymentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PremiumPaymentRepository extends JpaRepository<PremiumPayment, Long> {
    List<PremiumPayment> findByPolicyIdOrderByPaymentDateDesc(Long policyId);
    boolean existsByTransactionReference(String txnRef);
    Optional<PremiumPayment> findByTransactionReference(String txnRef);
    List<PremiumPayment> findByPaymentStatusAndCreatedAtBefore(PaymentStatus status, LocalDateTime before);
}
