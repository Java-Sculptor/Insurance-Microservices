package com.ampta.premiumservice.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PolicyDto {
    private Long policyId;
    private String policyNumber;
    private Long quoteId;
    private Long customerId;
    private Long productId;
    private LocalDate issueDate;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private String policyStatus;
    private String premiumFrequency;
    private BigDecimal finalPremium;
}

