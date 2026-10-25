package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.InstallmentStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponseDto {

    private Long scheduleId;
    private Long policyId;
    private Integer installmentNo;
    private LocalDate dueDate;
    private BigDecimal premiumAmount;
    private BigDecimal lateFee;
    private Integer gracePeriodDays;
    private BigDecimal totalPayable;
    private String status;
    private LocalDate paidDate;
    private LocalDateTime createdAt;

}
