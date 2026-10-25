package com.logger.agentmodule.controller;
import com.logger.agentmodule.dto.AgentPolicyAssignmentRequest;
import com.logger.agentmodule.dto.AgentPolicyResponse;
import com.logger.agentmodule.service.AgentPolicyService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents/{agentId}/policies")
public class AgentPolicyController {

    private final AgentPolicyService agentPolicyService;

    public AgentPolicyController(
            AgentPolicyService agentPolicyService) {
        this.agentPolicyService = agentPolicyService;
    }

    // Assign policy to an agent
    @PostMapping
    public ResponseEntity<AgentPolicyResponse> assignPolicy(
            @PathVariable Long agentId,
            @Valid @RequestBody AgentPolicyAssignmentRequest request) {

        AgentPolicyResponse response =
                agentPolicyService.assignPolicy(agentId, request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all policies assigned to an agent
    @GetMapping
    public ResponseEntity<List<AgentPolicyResponse>> getPoliciesByAgentId(
            @PathVariable Long agentId) {

        return ResponseEntity.ok(
                agentPolicyService.getPoliciesByAgentId(agentId));
    }

    // Get a specific policy assigned to an agent
    @GetMapping("/{policyId}")
    public ResponseEntity<AgentPolicyResponse> getPolicyById(
            @PathVariable Long agentId,
            @PathVariable Long policyId) {

        return ResponseEntity.ok(
                agentPolicyService.getPolicyByAgentIdAndPolicyId(
                        agentId, policyId));
    }
}