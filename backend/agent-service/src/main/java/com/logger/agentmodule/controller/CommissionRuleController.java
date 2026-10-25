package com.logger.agentmodule.controller;
import com.logger.agentmodule.dto.CommissionRuleCreateRequest;
import com.logger.agentmodule.dto.CommissionRuleResponse;
import com.logger.agentmodule.service.CommissionRuleService;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/commission-rules")
public class CommissionRuleController {

    private final CommissionRuleService commissionRuleService;

    public CommissionRuleController(
            CommissionRuleService commissionRuleService) {
        this.commissionRuleService = commissionRuleService;
    }

    // Create commission rule
    @PostMapping
    public ResponseEntity<CommissionRuleResponse> createRule(
            @Valid @RequestBody CommissionRuleCreateRequest request) {

        CommissionRuleResponse response =
                commissionRuleService.createRule(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(response);
    }

    // Get all commission rules
    @GetMapping
    public ResponseEntity<List<CommissionRuleResponse>> getAllRules() {
        return ResponseEntity.ok(
                commissionRuleService.getAllRules());
    }

    // Get rules by product ID
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<CommissionRuleResponse>> getRulesByProductId(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                commissionRuleService.getRulesByProductId(productId));
    }

    // Update commission rule
    @PutMapping("/{ruleId}")
    public ResponseEntity<CommissionRuleResponse> updateRule(
            @PathVariable Long ruleId,
            @Valid @RequestBody CommissionRuleCreateRequest request) {

        return ResponseEntity.ok(
                commissionRuleService.updateRule(ruleId, request));
    }
}

