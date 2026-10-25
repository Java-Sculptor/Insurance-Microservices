package com.logger.agentmodule.controller;
import com.logger.agentmodule.dto.AgentCustomerAssignmentRequest;
import com.logger.agentmodule.dto.AgentCustomerResponse;
import com.logger.agentmodule.service.AgentCustomerService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents/{agentId}/customers")
public class AgentCustomerController {

    private final AgentCustomerService agentCustomerService;

    public AgentCustomerController(
            AgentCustomerService agentCustomerService) {
        this.agentCustomerService = agentCustomerService;
    }

    // Assign customer to agent
    @PostMapping
    public ResponseEntity<AgentCustomerResponse> assignCustomer(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentCustomerAssignmentRequest request) {

        AgentCustomerResponse response =
                agentCustomerService.assignCustomer(agentId, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get customers assigned to an agent
    @GetMapping
    public ResponseEntity<List<AgentCustomerResponse>> getCustomersByAgentId(
            @PathVariable Long agentId) {

        return ResponseEntity.ok(
                agentCustomerService.getCustomersByAgentId(agentId));
    }
}
