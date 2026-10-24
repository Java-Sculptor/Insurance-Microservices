
package com.app.insurance_management.mapper;

import com.app.insurance_management.dto.SettlementResponseDto;
import com.app.insurance_management.entity.ClaimSettlement;
import org.springframework.stereotype.Component;

@Component
public class ClaimSettlementMapper {

    public SettlementResponseDto toResponseDto(
            ClaimSettlement settlement) {

        if (settlement == null) {
            return null;
        }

        SettlementResponseDto response =
                new SettlementResponseDto();

        response.setSettlementId(
                settlement.getSettlementId());

        response.setClaimId(
                settlement.getClaim().getClaimId());

        response.setClaimNumber(
                settlement.getClaim().getClaimNumber());

        response.setApprovedAmount(
                settlement.getApprovedAmount());

        response.setPaymentMode(
                settlement.getPaymentMode());

        response.setTransactionReference(
                settlement.getTransactionReference());

        response.setSettlementDate(
                settlement.getSettlementDate());

        response.setSettlementStatus(
                settlement.getSettlementStatus());

        response.setSettlementAdvicePath(
                settlement.getSettlementAdvicePath());

        response.setRemarks(
                settlement.getRemarks());

        return response;
    }
}
