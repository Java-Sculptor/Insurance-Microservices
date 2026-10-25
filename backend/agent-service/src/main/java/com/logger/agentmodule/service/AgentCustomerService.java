package com.logger.agentmodule.service;
import com.logger.agentmodule.dto.AgentCustomerAssignmentRequest;
import com.logger.agentmodule.dto.AgentCustomerResponse;
import com.logger.agentmodule.dto.AgentCustomerTransferRequest;

import java.util.List;

public interface AgentCustomerService {

    AgentCustomerResponse assignCustomer(
            Long agentId,
            AgentCustomerAssignmentRequest request);

    List<AgentCustomerResponse> getCustomersByAgentId(Long agentId);

    AgentCustomerResponse transferCustomer(
            Long agentId,
            Long customerId,
            AgentCustomerTransferRequest request
    );
}
