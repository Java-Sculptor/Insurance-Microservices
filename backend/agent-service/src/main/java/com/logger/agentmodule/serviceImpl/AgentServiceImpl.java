package com.logger.agentmodule.serviceImpl;

import com.logger.agentmodule.dto.AgentCreateRequest;
import com.logger.agentmodule.dto.AgentResponse;
import com.logger.agentmodule.dto.AgentUpdateRequest;
import com.logger.agentmodule.entity.Agent;
import com.logger.agentmodule.enums.AgentStatus;
import com.logger.agentmodule.repository.AgentRepository;
import com.logger.agentmodule.service.AgentService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    public AgentServiceImpl(AgentRepository agentRepository) {
        this.agentRepository = agentRepository;
    }

    @Override
    public AgentResponse createAgent(AgentCreateRequest request) {

        if (agentRepository.existsByAgentCode(request.getAgentCode())) {
            throw new RuntimeException("Agent code already exists");
        }

        if (agentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        Agent agent = new Agent();

        agent.setUserId(request.getUserId());
        agent.setAgentCode(request.getAgentCode());
        agent.setFirstName(request.getFirstName());
        agent.setLastName(request.getLastName());
        agent.setEmail(request.getEmail());
        agent.setMobileNo(request.getMobileNo());
        agent.setJoiningDate(request.getJoiningDate());
        agent.setLicenseNumber(request.getLicenseNumber());

        agent.setStatus(AgentStatus.Pending);

        Agent savedAgent = agentRepository.save(agent);

        AgentResponse response = new AgentResponse();

        response.setAgentId(savedAgent.getAgentId());
        response.setUserId(savedAgent.getUserId());
        response.setAgentCode(savedAgent.getAgentCode());
        response.setFirstName(savedAgent.getFirstName());
        response.setLastName(savedAgent.getLastName());
        response.setEmail(savedAgent.getEmail());
        response.setMobileNo(savedAgent.getMobileNo());
        response.setJoiningDate(savedAgent.getJoiningDate());
        response.setLicenseNumber(savedAgent.getLicenseNumber());
        response.setStatus(savedAgent.getStatus());
        response.setCreatedAt(savedAgent.getCreatedAt());
        response.setUpdatedAt(savedAgent.getUpdatedAt());

        return response;
    }


    @Override
    public AgentResponse getAgentById(Long agentId) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new RuntimeException("Agent not found with ID: " + agentId)
                );

        AgentResponse response = new AgentResponse();

        response.setAgentId(agent.getAgentId());
        response.setUserId(agent.getUserId());
        response.setAgentCode(agent.getAgentCode());
        response.setFirstName(agent.getFirstName());
        response.setLastName(agent.getLastName());
        response.setEmail(agent.getEmail());
        response.setMobileNo(agent.getMobileNo());
        response.setJoiningDate(agent.getJoiningDate());
        response.setLicenseNumber(agent.getLicenseNumber());
        response.setStatus(agent.getStatus());
        response.setCreatedAt(agent.getCreatedAt());
        response.setUpdatedAt(agent.getUpdatedAt());

        return response;
    }

    @Override
    public List<AgentResponse> getAllAgents() {

        List<Agent> agents = agentRepository.findAll();

        return agents.stream()
                .map(agent -> {
                    AgentResponse response = new AgentResponse();

                    response.setAgentId(agent.getAgentId());
                    response.setUserId(agent.getUserId());
                    response.setAgentCode(agent.getAgentCode());
                    response.setFirstName(agent.getFirstName());
                    response.setLastName(agent.getLastName());
                    response.setEmail(agent.getEmail());
                    response.setMobileNo(agent.getMobileNo());
                    response.setJoiningDate(agent.getJoiningDate());
                    response.setLicenseNumber(agent.getLicenseNumber());
                    response.setStatus(agent.getStatus());
                    response.setCreatedAt(agent.getCreatedAt());
                    response.setUpdatedAt(agent.getUpdatedAt());

                    return response;
                })
                .toList();
    }



    @Override
    public AgentResponse updateAgent(Long agentId, AgentUpdateRequest request) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new RuntimeException("Agent not found with ID: " + agentId)
                );

        if (!agent.getEmail().equalsIgnoreCase(request.getEmail())
                && agentRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Email already exists");
        }

        agent.setUserId(request.getUserId());
        agent.setFirstName(request.getFirstName());
        agent.setLastName(request.getLastName());
        agent.setEmail(request.getEmail());
        agent.setMobileNo(request.getMobileNo());
        agent.setJoiningDate(request.getJoiningDate());
        agent.setLicenseNumber(request.getLicenseNumber());

        Agent updatedAgent = agentRepository.save(agent);

        AgentResponse response = new AgentResponse();
        response.setAgentId(updatedAgent.getAgentId());
        response.setUserId(updatedAgent.getUserId());
        response.setAgentCode(updatedAgent.getAgentCode());
        response.setFirstName(updatedAgent.getFirstName());
        response.setLastName(updatedAgent.getLastName());
        response.setEmail(updatedAgent.getEmail());
        response.setMobileNo(updatedAgent.getMobileNo());
        response.setJoiningDate(updatedAgent.getJoiningDate());
        response.setLicenseNumber(updatedAgent.getLicenseNumber());
        response.setStatus(updatedAgent.getStatus());
        response.setCreatedAt(updatedAgent.getCreatedAt());
        response.setUpdatedAt(updatedAgent.getUpdatedAt());

        return response;
    }
}
