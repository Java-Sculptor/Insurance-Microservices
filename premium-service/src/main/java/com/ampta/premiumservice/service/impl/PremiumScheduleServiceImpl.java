package com.ampta.premiumservice.service.impl;

import com.ampta.premiumservice.client.PolicyClient;
import com.ampta.premiumservice.dto.ApiResponse;
import com.ampta.premiumservice.dto.PolicyDto;
import com.ampta.premiumservice.dto.ScheduleRequestDto;
import com.ampta.premiumservice.dto.ScheduleResponseDto;
import com.ampta.premiumservice.dto.ScheduleUpdateDto;
import com.ampta.premiumservice.entity.PremiumSchedule;
import com.ampta.premiumservice.entity.enums.InstallmentStatus;
import com.ampta.premiumservice.exception.BusinessException;
import com.ampta.premiumservice.exception.ResourceNotFoundException;
import com.ampta.premiumservice.repository.PremiumScheduleRepository;
import com.ampta.premiumservice.service.PremiumScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class PremiumScheduleServiceImpl implements PremiumScheduleService {

    private final PremiumScheduleRepository scheduleRepository;
    private final PolicyClient policyClient;

    @Value("${premium.penalty-per-day:10}")
    private BigDecimal penaltyPerDay;

    @Value("${premium.max-penalty-amount:5000}")
    private BigDecimal maxPenaltyAmount;

    @Override
    public List<ScheduleResponseDto> generateSchedule(ScheduleRequestDto request) {
        if (request.getPolicyId() == null) {
            throw new ResourceNotFoundException("Policy ID is mandatory to generate schedules");
        }

        // 1. Consume Policy Insurance Service via OpenFeign
        PolicyDto policy = null;
        try {
            ApiResponse<PolicyDto> response = policyClient.getPolicy(request.getPolicyId());
            if (response != null && response.getData() != null) {
                policy = response.getData();
                log.info("Successfully fetched policy details from Policy Service: PolicyId={}, Number={}, Status={}",
                        policy.getPolicyId(), policy.getPolicyNumber(), policy.getPolicyStatus());
            }
        } catch (Exception e) {
            log.warn("Could not retrieve policy ID: {} from policy service: {}", request.getPolicyId(), e.getMessage());
        }

        // 2. Validate policy status from Policy Service
        if (policy != null && policy.getPolicyStatus() != null) {
            String status = policy.getPolicyStatus().toUpperCase();
            if ("CANCELLED".equals(status) || "EXPIRED".equals(status) || "CLOSED".equals(status)) {
                throw new BusinessException("Cannot generate premium schedule. Policy is in " + status + " status.");
            }

            // Auto-enrich missing fields directly from Policy Service
            if (request.getCustomerId() == null && policy.getCustomerId() != null) {
                request.setCustomerId(policy.getCustomerId());
            }
            if (request.getStartDate() == null && policy.getEffectiveDate() != null) {
                request.setStartDate(policy.getEffectiveDate());
            }
            if ((request.getPolicyTermMonths() == null || request.getPolicyTermMonths() <= 0)
                    && policy.getEffectiveDate() != null && policy.getExpiryDate() != null) {
                int months = (int) ChronoUnit.MONTHS.between(policy.getEffectiveDate(), policy.getExpiryDate());
                request.setPolicyTermMonths(months > 0 ? months : 12);
            }
            if (request.getFinalPremium() == null && policy.getFinalPremium() != null) {
                request.setFinalPremium(policy.getFinalPremium());
            }
        }

        if (request.getFinalPremium() == null || request.getFinalPremium().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("Final premium amount must be specified and greater than zero");
        }

        String freq = request.getPremiumFrequency() != null ? request.getPremiumFrequency().toUpperCase().trim() : "YEARLY";
        int termMonths = request.getPolicyTermMonths() != null && request.getPolicyTermMonths() > 0
                ? request.getPolicyTermMonths() : 12;

        int count;
        int intervalMonths;
        int gracePeriodDays;

        switch (freq) {
            case "MONTHLY" -> {
                count = termMonths;
                intervalMonths = 1;
                gracePeriodDays = 15;
            }
            case "QUARTERLY" -> {
                count = Math.max(1, termMonths / 3);
                intervalMonths = 3;
                gracePeriodDays = 30;
            }
            case "HALF_YEARLY", "HALF-YEARLY" -> {
                count = Math.max(1, termMonths / 6);
                intervalMonths = 6;
                gracePeriodDays = 30;
            }
            case "YEARLY" -> {
                count = Math.max(1, termMonths / 12);
                intervalMonths = 12;
                gracePeriodDays = 30;
            }
            case "SINGLE_PREMIUM", "SINGLE" -> {
                count = 1;
                intervalMonths = termMonths;
                gracePeriodDays = 30;
            }
            default -> throw new ResourceNotFoundException("Invalid premium frequency: " + request.getPremiumFrequency());
        }

        BigDecimal installmentAmount = request.getFinalPremium()
                .divide(BigDecimal.valueOf(count), 2, RoundingMode.HALF_UP);

        List<PremiumSchedule> schedules = new ArrayList<>();
        LocalDate startDate = request.getStartDate() != null ? request.getStartDate() : LocalDate.now();

        BigDecimal runningTotal = BigDecimal.ZERO;
        for (int i = 1; i <= count; i++) {
            PremiumSchedule schedule = new PremiumSchedule();
            schedule.setPolicyId(request.getPolicyId());
            schedule.setInstallmentNo(i);

            // Adjust any cent rounding discrepancies on the final installment
            if (i == count) {
                schedule.setPremiumAmount(request.getFinalPremium().subtract(runningTotal));
            } else {
                schedule.setPremiumAmount(installmentAmount);
                runningTotal = runningTotal.add(installmentAmount);
            }

            // Installment 1 is due on policy start date; subsequent installments every intervalMonths
            schedule.setDueDate(startDate.plusMonths((long) (i - 1) * intervalMonths));
            schedule.setLateFee(BigDecimal.ZERO);
            schedule.setGracePeriodDays(gracePeriodDays);
            schedule.setStatus(InstallmentStatus.PENDING);
            schedules.add(schedule);
        }

        log.info("Generated {} installment schedules for Policy ID: {}", schedules.size(), request.getPolicyId());
        return scheduleRepository.saveAll(schedules)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ScheduleResponseDto> generateScheduleFromPolicy(Long policyId, String frequency, BigDecimal finalPremium) {
        PolicyDto policy = getPolicyDetails(policyId);

        ScheduleRequestDto request = new ScheduleRequestDto();
        request.setPolicyId(policyId);
        request.setCustomerId(policy.getCustomerId());
        request.setStartDate(policy.getEffectiveDate() != null ? policy.getEffectiveDate() : LocalDate.now());

        int termMonths = 12;
        if (policy.getEffectiveDate() != null && policy.getExpiryDate() != null) {
            int calculated = (int) ChronoUnit.MONTHS.between(policy.getEffectiveDate(), policy.getExpiryDate());
            if (calculated > 0) termMonths = calculated;
        }
        request.setPolicyTermMonths(termMonths);
        request.setPremiumFrequency(frequency != null && !frequency.isBlank() ? frequency : "YEARLY");
        request.setFinalPremium(finalPremium != null ? finalPremium : (policy.getFinalPremium() != null ? policy.getFinalPremium() : BigDecimal.valueOf(12000)));

        return generateSchedule(request);
    }

    @Override
    @Transactional(readOnly = true)
    public PolicyDto getPolicyDetails(Long policyId) {
        try {
            ApiResponse<PolicyDto> response = policyClient.getPolicy(policyId);
            if (response != null && response.getData() != null) {
                return response.getData();
            }
            throw new ResourceNotFoundException("Policy details not found in Policy Service for policy ID: " + policyId);
        } catch (Exception e) {
            log.error("Failed to fetch policy ID: {} from policy service: {}", policyId, e.getMessage());
            throw new ResourceNotFoundException("Could not retrieve policy from Policy Service for ID: " + policyId + " - " + e.getMessage());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getScheduleByPolicy(Long policyId) {
        return scheduleRepository.findByPolicyIdOrderByInstallmentNoAsc(policyId)

                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ScheduleResponseDto updateInstallment(Long scheduleId, ScheduleUpdateDto dto) {
        PremiumSchedule schedule = scheduleRepository.findById(scheduleId)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule not found with ID: " + scheduleId));

        if (dto.getStatus() != null && !dto.getStatus().isBlank()) {
            schedule.setStatus(InstallmentStatus.valueOf(dto.getStatus().toUpperCase()));
        }
        if (dto.getLateFee() != null) {
            schedule.setLateFee(dto.getLateFee());
        }

        PremiumSchedule updated = scheduleRepository.save(schedule);
        return toDto(updated);
    }

    @Override
    public int assessOverdueAndCalculateLateFees() {
        LocalDate today = LocalDate.now();
        List<InstallmentStatus> unpaidStatuses = List.of(InstallmentStatus.PENDING, InstallmentStatus.PARTIAL_PAID, InstallmentStatus.OVERDUE);
        List<PremiumSchedule> pastDueSchedules = scheduleRepository.findByStatusInAndDueDateBefore(unpaidStatuses, today);

        int updatedCount = 0;
        for (PremiumSchedule schedule : pastDueSchedules) {
            int grace = schedule.getGracePeriodDays() != null ? schedule.getGracePeriodDays() : 30;
            LocalDate graceExpiryDate = schedule.getDueDate().plusDays(grace);

            if (today.isAfter(graceExpiryDate)) {
                long daysOverdue = ChronoUnit.DAYS.between(graceExpiryDate, today);
                BigDecimal lateFee = penaltyPerDay.multiply(BigDecimal.valueOf(daysOverdue));

                if (maxPenaltyAmount != null && lateFee.compareTo(maxPenaltyAmount) > 0) {
                    lateFee = maxPenaltyAmount;
                }

                boolean changed = false;
                if (schedule.getStatus() != InstallmentStatus.OVERDUE) {
                    schedule.setStatus(InstallmentStatus.OVERDUE);
                    changed = true;
                }
                if (schedule.getLateFee() == null || schedule.getLateFee().compareTo(lateFee) != 0) {
                    schedule.setLateFee(lateFee);
                    changed = true;
                }

                if (changed) {
                    scheduleRepository.save(schedule);
                    updatedCount++;
                }
            }
        }

        if (updatedCount > 0) {
            log.info("Overdue Scheduler updated {} installment schedules with late fee penalties.", updatedCount);
        }
        return updatedCount;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduleResponseDto> getPendingSchedulesByDueDate(LocalDate targetDate) {
        return scheduleRepository.findByStatusInAndDueDate(
                List.of(InstallmentStatus.PENDING, InstallmentStatus.PARTIAL_PAID, InstallmentStatus.OVERDUE),
                targetDate
        ).stream().map(this::toDto).collect(Collectors.toList());
    }

    public ScheduleResponseDto toDto(PremiumSchedule s) {
        BigDecimal late = s.getLateFee() != null ? s.getLateFee() : BigDecimal.ZERO;
        BigDecimal premium = s.getPremiumAmount() != null ? s.getPremiumAmount() : BigDecimal.ZERO;
        return ScheduleResponseDto.builder()
                .scheduleId(s.getScheduleId())
                .policyId(s.getPolicyId())
                .installmentNo(s.getInstallmentNo())
                .dueDate(s.getDueDate())
                .premiumAmount(premium)
                .lateFee(late)
                .gracePeriodDays(s.getGracePeriodDays())
                .totalPayable(premium.add(late))
                .status(s.getStatus() != null ? s.getStatus().name() : InstallmentStatus.PENDING.name())
                .paidDate(s.getPaidDate())
                .createdAt(s.getCreatedAt())
                .build();
    }
}
