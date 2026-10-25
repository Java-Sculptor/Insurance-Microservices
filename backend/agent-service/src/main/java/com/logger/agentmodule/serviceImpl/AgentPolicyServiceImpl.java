package com.logger.agentmodule.serviceImpl;
import com.logger.agentmodule.dto.AgentPolicyAssignmentRequest;
import com.logger.agentmodule.dto.AgentPolicyResponse;
import com.logger.agentmodule.entity.Agent;
import com.logger.agentmodule.entity.AgentPolicy;
import com.logger.agentmodule.enums.AgentPolicyStatus;
import com.logger.agentmodule.enums.AgentStatus;
import com.logger.agentmodule.repository.AgentPolicyRepository;
import com.logger.agentmodule.repository.AgentRepository;
import com.logger.agentmodule.service.AgentPolicyService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentPolicyServiceImpl implements AgentPolicyService {

    private final AgentRepository agentRepository;
    private final AgentPolicyRepository agentPolicyRepository;
    private final ModelMapper modelMapper;

    public AgentPolicyServiceImpl(
            AgentRepository agentRepository,
            AgentPolicyRepository agentPolicyRepository,
            ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.agentPolicyRepository = agentPolicyRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AgentPolicyResponse assignPolicy(
            Long agentId,
            AgentPolicyAssignmentRequest request) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new RuntimeException("Agent not found"));

        if (agent.getStatus() != AgentStatus.Active) {
            throw new RuntimeException(
                    "Only active agents can be assigned policies");
        }

        if (agentPolicyRepository
                .findByAgentIdAndPolicyId(
                        agentId, request.getPolicyId())
                .isPresent()) {
            throw new RuntimeException(
                    "Policy is already assigned to this agent");
        }

        AgentPolicy policy = new AgentPolicy();
        policy.setAgentId(agentId);
        policy.setPolicyId(request.getPolicyId());
        policy.setCustomerId(request.getCustomerId());
        policy.setAssignedDate(LocalDateTime.now());
        policy.setStatus(AgentPolicyStatus.Assigned);

        AgentPolicy savedPolicy =
                agentPolicyRepository.save(policy);

        return modelMapper.map(
                savedPolicy, AgentPolicyResponse.class);
    }

    @Override
    public List<AgentPolicyResponse> getPoliciesByAgentId(
            Long agentId) {

        if (!agentRepository.existsById(agentId)) {
            throw new RuntimeException("Agent not found");
        }

        return agentPolicyRepository.findByAgentId(agentId)
                .stream()
                .map(policy -> modelMapper.map(
                        policy, AgentPolicyResponse.class))
                .toList();
    }

    @Override
    public AgentPolicyResponse getPolicyByAgentIdAndPolicyId(
            Long agentId, Long policyId) {

        AgentPolicy policy = agentPolicyRepository
                .findByAgentIdAndPolicyId(agentId, policyId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Policy not found for this agent"));

        return modelMapper.map(
                policy, AgentPolicyResponse.class);
    }
}