package com.logger.agentmodule.service;
import com.logger.agentmodule.entity.CommissionPayment;

import java.util.List;

public interface CommissionPaymentService {

    List<CommissionPayment> processPayouts(String paymentMode);

    List<CommissionPayment> getAllPayouts();

    List<CommissionPayment> getPayoutsByAgentId(Long agentId);
}

