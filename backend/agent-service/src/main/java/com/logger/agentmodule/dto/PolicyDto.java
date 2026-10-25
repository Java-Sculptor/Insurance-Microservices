package com.logger.agentmodule.dto;
import lombok.Data;
import java.time.LocalDate;

@Data
public class PolicyDto {

    private Long policyId;
    private Long customerId;
    private LocalDate effectiveDate;
    private LocalDate expiryDate;
    private LocalDate issueDate;
    private String policyNumber;
    private String policyStatus;
    private Long productId;
    private Long quoteId;
}