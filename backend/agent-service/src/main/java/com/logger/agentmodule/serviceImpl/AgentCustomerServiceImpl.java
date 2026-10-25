package com.logger.agentmodule.serviceImpl;
import com.logger.agentmodule.dto.AgentCustomerAssignmentRequest;
import com.logger.agentmodule.dto.AgentCustomerResponse;
import com.logger.agentmodule.dto.AgentCustomerTransferRequest;
import com.logger.agentmodule.entity.Agent;
import com.logger.agentmodule.entity.AgentCustomer;
import com.logger.agentmodule.enums.AgentCustomerStatus;
import com.logger.agentmodule.enums.AgentStatus;
import com.logger.agentmodule.repository.AgentCustomerRepository;
import com.logger.agentmodule.repository.AgentRepository;
import com.logger.agentmodule.service.AgentCustomerService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentCustomerServiceImpl implements AgentCustomerService {

    private final AgentRepository agentRepository;
    private final AgentCustomerRepository agentCustomerRepository;
    private final ModelMapper modelMapper;


    public AgentCustomerServiceImpl(
            AgentRepository agentRepository,
            AgentCustomerRepository agentCustomerRepository,
            ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.agentCustomerRepository = agentCustomerRepository;
        this.modelMapper=modelMapper;
    }

    @Override
    public AgentCustomerResponse assignCustomer(
            Long agentId,
            AgentCustomerAssignmentRequest request) {

        Agent agent = agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new RuntimeException("Agent not found: " + agentId));

        if (agent.getStatus() != com.logger.agentmodule.enums.AgentStatus.Active) {
            throw new RuntimeException("Only active agents can receive customers");
        }

        boolean alreadyAssigned = agentCustomerRepository
                .findByAgentIdAndStatus(agentId, AgentCustomerStatus.Active)
                .stream()
                .anyMatch(ac -> ac.getCustomerId().equals(request.getCustomerId()));

        if (alreadyAssigned) {
            throw new RuntimeException("Customer is already assigned to this agent");
        }

        AgentCustomer customer = new AgentCustomer();
        customer.setAgentId(agentId);
        customer.setCustomerId(request.getCustomerId());
        customer.setAssignedBy(request.getAssignedBy());
        customer.setAssignedDate(LocalDateTime.now());
        customer.setStatus(AgentCustomerStatus.Active);

        AgentCustomer saved = agentCustomerRepository.save(customer);

        AgentCustomerResponse response = new AgentCustomerResponse();
        response.setAgentCustomerId(saved.getAgentCustomerId());
        response.setAgentId(saved.getAgentId());
        response.setCustomerId(saved.getCustomerId());
        response.setAssignedDate(saved.getAssignedDate());
        response.setAssignedBy(saved.getAssignedBy());
        response.setStatus(saved.getStatus());
        response.setCreatedAt(saved.getCreatedAt());

        return response;
    }

    @Override
    public List<AgentCustomerResponse> getCustomersByAgentId(Long agentId) {

        if (!agentRepository.existsById(agentId)) {
            throw new RuntimeException("Agent not found: " + agentId);
        }

        return agentCustomerRepository.findByAgentId(agentId)
                .stream()
                .map(saved -> {
                    AgentCustomerResponse response = new AgentCustomerResponse();
                    response.setAgentCustomerId(saved.getAgentCustomerId());
                    response.setAgentId(saved.getAgentId());
                    response.setCustomerId(saved.getCustomerId());
                    response.setAssignedDate(saved.getAssignedDate());
                    response.setAssignedBy(saved.getAssignedBy());
                    response.setStatus(saved.getStatus());
                    response.setCreatedAt(saved.getCreatedAt());
                    return response;
                })
                .toList();
    }

    @Override
    public AgentCustomerResponse transferCustomer(
            Long agentId,
            Long customerId,
            AgentCustomerTransferRequest request) {

        // Check existing agent
        agentRepository.findById(agentId)
                .orElseThrow(() ->
                        new RuntimeException("Current agent not found"));

        // Check new agent
        Agent newAgent = agentRepository.findById(request.getNewAgentId())
                .orElseThrow(() ->
                        new RuntimeException("New agent not found"));

        if (newAgent.getStatus() != AgentStatus.Active) {
            throw new RuntimeException("New agent must be active");
        }

        // Find current active assignment
        AgentCustomer oldAssignment = agentCustomerRepository
                .findByAgentIdAndCustomerIdAndStatus(
                        agentId,
                        customerId,
                        AgentCustomerStatus.Active)
                .orElseThrow(() ->
                        new RuntimeException("Active customer assignment not found"));

        if (agentId.equals(request.getNewAgentId())) {
            throw new RuntimeException(
                    "Customer is already assigned to this agent");
        }

        // Mark old assignment as transferred
        oldAssignment.setStatus(AgentCustomerStatus.Transferred);
        agentCustomerRepository.save(oldAssignment);

        // Create new assignment
        AgentCustomer newAssignment = new AgentCustomer();
        newAssignment.setAgentId(request.getNewAgentId());
        newAssignment.setCustomerId(customerId);
        newAssignment.setAssignedBy(request.getAssignedBy());
        newAssignment.setAssignedDate(LocalDateTime.now());
        newAssignment.setStatus(AgentCustomerStatus.Active);

        AgentCustomer savedAssignment =
                agentCustomerRepository.save(newAssignment);

        return modelMapper.map(
                savedAssignment,
                AgentCustomerResponse.class
        );    }

}
