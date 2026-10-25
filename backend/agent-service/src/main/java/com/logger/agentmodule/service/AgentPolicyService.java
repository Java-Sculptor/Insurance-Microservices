package com.logger.agentmodule.service;

import com.logger.agentmodule.dto.AgentPolicyAssignmentRequest;
import com.logger.agentmodule.dto.AgentPolicyResponse;

import java.util.List;

public interface AgentPolicyService {

    AgentPolicyResponse assignPolicy(
            Long agentId,
            AgentPolicyAssignmentRequest request);

    List<AgentPolicyResponse> getPoliciesByAgentId(Long agentId);

    AgentPolicyResponse getPolicyByAgentIdAndPolicyId(
            Long agentId,
            Long policyId);
}
