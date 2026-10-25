package com.logger.agentmodule.service;


import com.logger.agentmodule.dto.AgentCommissionCalculateRequest;
import com.logger.agentmodule.dto.AgentCommissionResponse;
import com.logger.agentmodule.dto.AgentCommissionStatusUpdateRequest;

import java.util.List;

public interface AgentCommissionService {

    AgentCommissionResponse calculateCommission(
            AgentCommissionCalculateRequest request);

    List<AgentCommissionResponse> getAllCommissions();

    List<AgentCommissionResponse> getCommissionsByAgentId(
            Long agentId);

    AgentCommissionResponse updateCommissionStatus(
            Long commissionId,
            AgentCommissionStatusUpdateRequest request);
}

