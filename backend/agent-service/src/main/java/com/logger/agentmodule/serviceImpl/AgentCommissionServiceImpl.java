package com.logger.agentmodule.serviceImpl;

import com.logger.agentmodule.dto.AgentCommissionCalculateRequest;
import com.logger.agentmodule.dto.AgentCommissionResponse;
import com.logger.agentmodule.dto.AgentCommissionStatusUpdateRequest;
import com.logger.agentmodule.entity.Agent;
import com.logger.agentmodule.entity.AgentCommission;
import com.logger.agentmodule.entity.CommissionRule;
import com.logger.agentmodule.enums.AgentCommissionStatus;
import com.logger.agentmodule.enums.AgentStatus;
import com.logger.agentmodule.repository.AgentCommissionRepository;
import com.logger.agentmodule.repository.AgentRepository;
import com.logger.agentmodule.repository.CommissionRuleRepository;
import com.logger.agentmodule.service.AgentCommissionService;

import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AgentCommissionServiceImpl
        implements AgentCommissionService {

    private final AgentRepository agentRepository;
    private final CommissionRuleRepository commissionRuleRepository;
    private final AgentCommissionRepository agentCommissionRepository;
    private final ModelMapper modelMapper;

    public AgentCommissionServiceImpl(
            AgentRepository agentRepository,
            CommissionRuleRepository commissionRuleRepository,
            AgentCommissionRepository agentCommissionRepository,
            ModelMapper modelMapper) {
        this.agentRepository = agentRepository;
        this.commissionRuleRepository = commissionRuleRepository;
        this.agentCommissionRepository = agentCommissionRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional
    public AgentCommissionResponse calculateCommission(
            AgentCommissionCalculateRequest request) {

        Agent agent = agentRepository.findById(request.getAgentId())
                .orElseThrow(() ->
                        new RuntimeException("Agent not found"));

        if (agent.getStatus() != AgentStatus.Active) {
            throw new RuntimeException(
                    "Commission can only be calculated for active agents");
        }

        BigDecimal premium = request.getPremiumAmount();

        if (premium == null || premium.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Premium amount must be greater than zero");
        }

        LocalDate today = LocalDate.now();

        CommissionRule rule = commissionRuleRepository
                .findByProductIdAndIsActiveTrue(request.getProductId())
                .stream()
                .filter(r -> r.getEffectiveFrom() != null)
                .filter(r -> !r.getEffectiveFrom().isAfter(today))
                .filter(r -> r.getEffectiveTo() == null
                        || !r.getEffectiveTo().isBefore(today))
                .findFirst()
                .orElseThrow(() ->
                        new RuntimeException(
                                "No applicable commission rule found"));

        BigDecimal percentage = rule.getCommissionPercentage();

        BigDecimal commissionAmount = premium
                .multiply(percentage)
                .divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);

        AgentCommission commission = new AgentCommission();
        commission.setAgentId(request.getAgentId());
        commission.setPolicyId(request.getPolicyId());
        commission.setPremiumAmount(premium);
        commission.setCommissionPercentage(percentage);
        commission.setCommissionAmount(commissionAmount);
        commission.setStatus(AgentCommissionStatus.Pending);
        commission.setEarnedDate(LocalDateTime.now());
        commission.setCreatedAt(LocalDateTime.now());

        AgentCommission saved =
                agentCommissionRepository.save(commission);

        return modelMapper.map(saved, AgentCommissionResponse.class);
    }


    @Override
    public List<AgentCommissionResponse> getAllCommissions() {
        return agentCommissionRepository.findAll()
                .stream()
                .map(commission ->
                        modelMapper.map(
                                commission,
                                AgentCommissionResponse.class))
                .toList();
    }

    @Override
    public List<AgentCommissionResponse> getCommissionsByAgentId(
            Long agentId) {

        if (!agentRepository.existsById(agentId)) {
            throw new RuntimeException(
                    "Agent not found with id: " + agentId);
        }

        return agentCommissionRepository.findByAgentId(agentId)
                .stream()
                .map(commission ->
                        modelMapper.map(
                                commission,
                                AgentCommissionResponse.class))
                .toList();
    }


    @Override
    public AgentCommissionResponse updateCommissionStatus(
            Long commissionId,
            AgentCommissionStatusUpdateRequest request) {

        AgentCommission commission = agentCommissionRepository
                .findById(commissionId)
                .orElseThrow(() -> new RuntimeException(
                        "Commission not found with id: " + commissionId));

        AgentCommissionStatus newStatus = request.getStatus();

        if (newStatus == null) {
            throw new IllegalArgumentException(
                    "Commission status is required");
        }

        commission.setStatus(newStatus);

        AgentCommission updatedCommission =
                agentCommissionRepository.save(commission);

        return modelMapper.map(
                updatedCommission,
                AgentCommissionResponse.class);
    }


}

