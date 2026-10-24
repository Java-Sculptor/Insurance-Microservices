package com.app.insurance_management.mapper;

import com.app.insurance_management.dto.ClaimDocumentResponse;
import com.app.insurance_management.entity.ClaimDocument;
import com.app.insurance_management.entity.ClaimMaster;
import org.springframework.stereotype.Component;

@Component
public class ClaimDocumentMapper {

    public ClaimDocument toEntity(
            ClaimMaster claim,
            String documentName,
            String filePath) {

        return ClaimDocument.builder()
                .claim(claim)
                .documentName(documentName)
                .filePath(filePath)
                .verificationStatus("PENDING")
                .build();
    }

    public ClaimDocumentResponse toResponse(
            ClaimDocument document) {

        return ClaimDocumentResponse.builder()
                .documentId(document.getDocumentId())
                .claimId(document.getClaim().getClaimId())
                .documentName(document.getDocumentName())
                .filePath(document.getFilePath())
                .verificationStatus(document.getVerificationStatus())
                .build();
    }
}