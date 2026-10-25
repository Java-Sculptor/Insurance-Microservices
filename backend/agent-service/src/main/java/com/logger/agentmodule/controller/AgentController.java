package com.logger.agentmodule.controller;
import com.logger.agentmodule.dto.*;
import com.logger.agentmodule.service.AgentCustomerService;
import com.logger.agentmodule.service.AgentService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
public class AgentController {

    private final AgentService agentService;
    private  final AgentCustomerService agentCustomerService;

    public AgentController(AgentService agentService,AgentCustomerService agentCustomerService) {
        this.agentService = agentService;
        this.agentCustomerService=agentCustomerService;
    }

    // Create Agent
    @PostMapping
    public ResponseEntity<AgentResponse> createAgent(
            @Valid @RequestBody AgentCreateRequest request) {

        AgentResponse response = agentService.createAgent(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get All Agents
    @GetMapping
    public ResponseEntity<List<AgentResponse>> getAllAgents() {

        return ResponseEntity.ok(agentService.getAllAgents());
    }

    // Get Agent By ID
    @GetMapping("/{agentId}")
    public ResponseEntity<AgentResponse> getAgentById(
            @PathVariable Long agentId) {

        return ResponseEntity.ok(agentService.getAgentById(agentId));
    }

    // Update Agent
    @PutMapping("/{agentId}")
    public ResponseEntity<AgentResponse> updateAgent(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentUpdateRequest request) {

        return ResponseEntity.ok(
                agentService.updateAgent(agentId, request));
    }
    @PutMapping("/{agentId}/customers/{customerId}/transfer")
    public ResponseEntity<AgentCustomerResponse> transferCustomer(
            @PathVariable("agentId") Long agentId,
            @PathVariable("customerId") Long customerId,
            @Valid @RequestBody AgentCustomerTransferRequest request) {

        AgentCustomerResponse response =
                agentCustomerService.transferCustomer(
                        agentId, customerId, request);

        return ResponseEntity.ok(response);
    }
}