package com.app.insurance_management.service;

import com.app.insurance_management.dto.ClaimDocumentRequest;
import com.app.insurance_management.dto.ClaimDocumentResponse;
import com.app.insurance_management.dto.DocumentVerificationRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ClaimDocumentService {

    public ClaimDocumentResponse addDocument(
            Long claimId,
            ClaimDocumentRequest request,
            MultipartFile file);

    List<ClaimDocumentResponse> getDocumentsByClaimId(
            Long claimId);

    ClaimDocumentResponse verifyDocument(
            Long claimId,
            Long documentId,
            DocumentVerificationRequest request);
}