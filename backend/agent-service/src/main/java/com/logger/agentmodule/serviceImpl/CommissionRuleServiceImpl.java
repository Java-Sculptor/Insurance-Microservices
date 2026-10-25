package com.logger.agentmodule.serviceImpl;

import com.logger.agentmodule.dto.CommissionRuleCreateRequest;
import com.logger.agentmodule.dto.CommissionRuleResponse;
import com.logger.agentmodule.entity.CommissionRule;
import com.logger.agentmodule.repository.CommissionRuleRepository;
import com.logger.agentmodule.service.CommissionRuleService;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.time.LocalDateTime.now;

@Service
public class CommissionRuleServiceImpl
        implements CommissionRuleService {

    private final CommissionRuleRepository commissionRuleRepository;
    private final ModelMapper modelMapper;

    public CommissionRuleServiceImpl(
            CommissionRuleRepository commissionRuleRepository,
            ModelMapper modelMapper) {
        this.commissionRuleRepository = commissionRuleRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public CommissionRuleResponse createRule(
            CommissionRuleCreateRequest request) {

        validateDates(request);

        CommissionRule rule =
                modelMapper.map(request, CommissionRule.class);

        rule.setCommissionRuleId(null);
        rule.setCreatedAt(now());

        if (rule.getIsActive() == null) {
            rule.setIsActive(true);
        }

        CommissionRule savedRule =
                commissionRuleRepository.save(rule);

        return modelMapper.map(
                savedRule, CommissionRuleResponse.class);
    }

    @Override
    public List<CommissionRuleResponse> getAllRules() {

        return commissionRuleRepository.findAll()
                .stream()
                .map(rule -> modelMapper.map(
                        rule, CommissionRuleResponse.class))
                .toList();
    }

    @Override
    public List<CommissionRuleResponse> getRulesByProductId(
            Long productId) {

        return commissionRuleRepository.findByProductId(productId)
                .stream()
                .map(rule -> modelMapper.map(
                        rule, CommissionRuleResponse.class))
                .toList();
    }

    @Override
    @Transactional
    public CommissionRuleResponse updateRule(
            Long ruleId,
            CommissionRuleCreateRequest request) {

        validateDates(request);

        CommissionRule rule = commissionRuleRepository
                .findById(ruleId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Commission rule not found: " + ruleId));

        modelMapper.map(request, rule);

        CommissionRule updatedRule =
                commissionRuleRepository.save(rule);

        return modelMapper.map(
                updatedRule, CommissionRuleResponse.class);
    }

    private void validateDates(
            CommissionRuleCreateRequest request) {

        if (request.getEffectiveFrom() == null) {
            throw new RuntimeException(
                    "Effective from date is required");
        }

        if (request.getEffectiveTo() != null
                && request.getEffectiveTo()
                .isBefore(request.getEffectiveFrom())) {
            throw new RuntimeException(
                    "Effective to date cannot be before effective from date");
        }
    }
}
