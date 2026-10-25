package com.logger.agentmodule.service;

import com.logger.agentmodule.dto.CommissionRuleCreateRequest;
import com.logger.agentmodule.dto.CommissionRuleResponse;

import java.util.List;

public interface CommissionRuleService {

    CommissionRuleResponse createRule(
            CommissionRuleCreateRequest request);

    List<CommissionRuleResponse> getAllRules();

    List<CommissionRuleResponse> getRulesByProductId(
            Long productId);

    CommissionRuleResponse updateRule(
            Long ruleId,
            CommissionRuleCreateRequest request);
}
