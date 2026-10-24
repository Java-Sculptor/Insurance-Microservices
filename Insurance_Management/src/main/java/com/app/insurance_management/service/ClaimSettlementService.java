
package com.app.insurance_management.service;

import com.app.insurance_management.dto.SettlementRequestDto;
import com.app.insurance_management.dto.SettlementResponseDto;

public interface ClaimSettlementService {

    SettlementResponseDto createSettlement(
            Long claimId,
            SettlementRequestDto request
    );

    SettlementResponseDto getSettlementByClaimId(Long claimId);
}
