package com.logger.agentmodule.serviceImpl;
import com.logger.agentmodule.entity.AgentCommission;
import com.logger.agentmodule.entity.CommissionPayment;
import com.logger.agentmodule.enums.AgentCommissionStatus;
import com.logger.agentmodule.repository.AgentCommissionRepository;
import com.logger.agentmodule.repository.CommissionPaymentRepository;
import com.logger.agentmodule.service.CommissionPaymentService;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class CommissionPaymentServiceImpl
        implements CommissionPaymentService {

    private final AgentCommissionRepository commissionRepository;
    private final CommissionPaymentRepository paymentRepository;

    public CommissionPaymentServiceImpl(
            AgentCommissionRepository commissionRepository,
            CommissionPaymentRepository paymentRepository) {
        this.commissionRepository = commissionRepository;
        this.paymentRepository = paymentRepository;
    }

    @Override
    @Transactional
    public List<CommissionPayment> processPayouts(String paymentMode) {

        List<AgentCommission> approvedCommissions =
                commissionRepository.findByStatus(
                        AgentCommissionStatus.Approved);

        List<CommissionPayment> payouts = new ArrayList<>();

        for (AgentCommission commission : approvedCommissions) {

            if (paymentRepository.existsByCommissionId(
                    commission.getCommissionId())) {
                continue;
            }

            CommissionPayment payment = new CommissionPayment();
            payment.setCommissionId(commission.getCommissionId());
            payment.setAgentId(commission.getAgentId());
            payment.setPaymentAmount(commission.getCommissionAmount());
            payment.setPaymentMode(paymentMode);
            payment.setStatus("Initiated");

            payouts.add(payment);
        }

        return paymentRepository.saveAll(payouts);
    }

    @Override
    public List<CommissionPayment> getAllPayouts() {
        return paymentRepository.findAll();
    }

    @Override
    public List<CommissionPayment> getPayoutsByAgentId(Long agentId) {
        return paymentRepository.findByAgentId(agentId);
    }
}

