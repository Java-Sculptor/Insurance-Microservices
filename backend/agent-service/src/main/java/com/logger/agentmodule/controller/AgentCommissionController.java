package com.logger.agentmodule.controller;
import com.logger.agentmodule.dto.AgentCommissionCalculateRequest;
import com.logger.agentmodule.dto.AgentCommissionResponse;
import com.logger.agentmodule.dto.AgentCommissionStatusUpdateRequest;
import com.logger.agentmodule.service.AgentCommissionService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/commissions")
public class AgentCommissionController {

    private final AgentCommissionService agentCommissionService;

    public AgentCommissionController(
            AgentCommissionService agentCommissionService) {
        this.agentCommissionService = agentCommissionService;
    }

    // Calculate commission
    @PostMapping("/calculate")
    public ResponseEntity<AgentCommissionResponse> calculateCommission(
            @Valid @RequestBody AgentCommissionCalculateRequest request) {

        AgentCommissionResponse response =
                agentCommissionService.calculateCommission(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<AgentCommissionResponse>>
    getAllCommissions() {
        return ResponseEntity.ok(
                agentCommissionService.getAllCommissions());
    }

    @GetMapping("/agent/{agentId}")
    public ResponseEntity<List<AgentCommissionResponse>>
    getCommissionsByAgentId(
            @PathVariable Long agentId) {
        return ResponseEntity.ok(
                agentCommissionService
                        .getCommissionsByAgentId(agentId));
    }

    @PatchMapping("/{commissionId}/status")
    public ResponseEntity<AgentCommissionResponse> updateCommissionStatus(
            @PathVariable Long commissionId,
            @Valid @RequestBody AgentCommissionStatusUpdateRequest request) {

        return ResponseEntity.ok(
                agentCommissionService.updateCommissionStatus(
                        commissionId, request));
    }


}

