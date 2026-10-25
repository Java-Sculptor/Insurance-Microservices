package com.ampta.premiumservice.dto;

import com.ampta.premiumservice.entity.enums.RefundStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RefundApprovalDto {
    @NotNull(message = "Approved by employee ID is mandatory")
    private Long approvedBy;

    @NotNull(message = "Refund status is mandatory (APPROVED or REJECTED)")
    private RefundStatus refundStatus;

    private String remarks;
}
