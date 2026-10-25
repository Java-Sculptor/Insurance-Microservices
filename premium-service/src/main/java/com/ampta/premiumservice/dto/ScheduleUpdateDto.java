package com.ampta.premiumservice.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ScheduleUpdateDto {
    private String status;
    private BigDecimal lateFee;
}
