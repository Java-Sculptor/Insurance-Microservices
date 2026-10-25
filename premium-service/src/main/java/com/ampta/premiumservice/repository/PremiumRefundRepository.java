package com.ampta.premiumservice.repository;

import com.ampta.premiumservice.entity.PremiumRefund;
import com.ampta.premiumservice.entity.enums.RefundStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PremiumRefundRepository extends JpaRepository<PremiumRefund, Long> {
    List<PremiumRefund> findByPolicyId(Long policyId);
    List<PremiumRefund> findByPaymentId(Long paymentId);
    List<PremiumRefund> findByRefundStatus(RefundStatus status);
}
