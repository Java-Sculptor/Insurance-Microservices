package com.logger.agentmodule.service;
import com.logger.agentmodule.dto.AgentCreateRequest;
import com.logger.agentmodule.dto.AgentResponse;
import com.logger.agentmodule.dto.AgentUpdateRequest;

import java.util.List;

public interface AgentService {
    AgentResponse createAgent(AgentCreateRequest request);

    List<AgentResponse> getAllAgents();

    AgentResponse getAgentById(Long agentId);

    AgentResponse updateAgent(Long agentId, AgentUpdateRequest request);
}