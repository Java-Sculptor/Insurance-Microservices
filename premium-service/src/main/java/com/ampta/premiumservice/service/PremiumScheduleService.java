package com.ampta.premiumservice.service;

import com.ampta.premiumservice.dto.PolicyDto;
import com.ampta.premiumservice.dto.ScheduleRequestDto;
import com.ampta.premiumservice.dto.ScheduleResponseDto;
import com.ampta.premiumservice.dto.ScheduleUpdateDto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface PremiumScheduleService {
    List<ScheduleResponseDto> generateSchedule(ScheduleRequestDto request);
    List<ScheduleResponseDto> generateScheduleFromPolicy(Long policyId, String frequency, BigDecimal finalPremium);
    PolicyDto getPolicyDetails(Long policyId);
    List<ScheduleResponseDto> getScheduleByPolicy(Long policyId);
    ScheduleResponseDto updateInstallment(Long scheduleId, ScheduleUpdateDto dto);
    int assessOverdueAndCalculateLateFees();
    List<ScheduleResponseDto> getPendingSchedulesByDueDate(LocalDate targetDate);
}

