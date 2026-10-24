
package com.app.insurance_management.mapper;

import com.app.insurance_management.dto.ClaimRequest;
import com.app.insurance_management.dto.ClaimResponse;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.util.ClaimNumberUtil;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class ClaimMasterMapper {

    public ClaimMaster toEntity(ClaimRequest request) {

        return ClaimMaster.builder()
                .claimNumber(ClaimNumberUtil.generateClaimNumber())
                .policyId(request.getPolicyId())
                .customerId(request.getCustomerId())
                .claimType(request.getClaimType())
                .incidentDate(request.getIncidentDate())
                .claimDate(LocalDate.now())
                .estimatedLoss(request.getEstimatedLoss())
                .claimStatus(ClaimStatus.REGISTERED)
                .build();
    }

    public ClaimResponse toResponse(ClaimMaster claimMaster) {

        return ClaimResponse.builder()
                .claimId(claimMaster.getClaimId())
                .claimNumber(claimMaster.getClaimNumber())
                .policyId(claimMaster.getPolicyId())
                .customerId(claimMaster.getCustomerId())
                .claimType(claimMaster.getClaimType())
                .incidentDate(claimMaster.getIncidentDate())
                .claimDate(claimMaster.getClaimDate())
                .estimatedLoss(claimMaster.getEstimatedLoss())
                .claimStatus(claimMaster.getClaimStatus().name())
                .build();
    }
}
