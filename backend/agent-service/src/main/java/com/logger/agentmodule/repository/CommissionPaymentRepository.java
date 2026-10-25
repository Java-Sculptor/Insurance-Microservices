package com.logger.agentmodule.repository;
import com.logger.agentmodule.entity.CommissionPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommissionPaymentRepository
        extends JpaRepository<CommissionPayment, Long> {

    List<CommissionPayment> findByAgentId(Long agentId);

    List<CommissionPayment> findByStatus(String status);

    List<CommissionPayment> findByPaymentMode(String paymentMode);

    List<CommissionPayment> findByPaymentModeAndStatus(
            String paymentMode,
            String status
    );

    boolean existsByCommissionId(Long commissionId);
}

