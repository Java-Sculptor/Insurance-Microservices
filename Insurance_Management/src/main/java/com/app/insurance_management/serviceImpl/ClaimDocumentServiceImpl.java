package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.exception.ClaimNotFoundException;
import com.app.insurance_management.dto.ClaimDocumentRequest;
import com.app.insurance_management.dto.ClaimDocumentResponse;
import com.app.insurance_management.dto.DocumentVerificationRequest;
import com.app.insurance_management.entity.ClaimDocument;
import com.app.insurance_management.entity.ClaimHistory;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.mapper.ClaimDocumentMapper;
import com.app.insurance_management.repository.ClaimDocumentRepository;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.service.ClaimDocumentService;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimDocumentServiceImpl implements ClaimDocumentService {

    private static final Logger log = LoggerFactory.getLogger(ClaimDocumentServiceImpl.class);

    private final ClaimDocumentRepository documentRepository;

    private final ClaimMasterRepository claimRepository;

    private final ClaimDocumentMapper documentMapper;

    private final DocumentStorageServiceImpl documentStorageService;

    private final ClaimHistoryRepository claimHistoryRepository;

    @Override
    @Transactional
    public ClaimDocumentResponse addDocument(
            Long claimId,
            ClaimDocumentRequest request,
            MultipartFile file) {

        // 1. Check whether the claim exists
        ClaimMaster claim = claimRepository.findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found with ID: " + claimId));
        String filePath = documentStorageService.storeFile(file);

        ClaimDocument document = documentMapper.toEntity(
                claim,
                request.getDocumentName(),
                filePath
        );

        // 3. Save document
        ClaimDocument savedDocument = documentRepository.save(document);

        // 4. Return response DTO
        return documentMapper.toResponse(savedDocument);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClaimDocumentResponse> getDocumentsByClaimId(
            Long claimId) {

        // 1. Check claim exists
        if (!claimRepository.existsById(claimId)) {
            throw new ClaimNotFoundException("Claim not found with ID: " + claimId);
        }

        // 2. Fetch documents and map to response DTOs
        return documentRepository
                .findByClaim_ClaimId(claimId)
                .stream()
                .map(documentMapper::toResponse)
                .toList();
    }

    @Override
    public ClaimDocumentResponse verifyDocument(Long claimId, Long documentId, DocumentVerificationRequest request) {

        ClaimMaster claim=claimRepository.findById(claimId)
                .orElseThrow(()-> new ClaimNotFoundException("Claim not found with ID: " + claimId));

        ClaimDocument document=documentRepository.findById(documentId)
                .orElseThrow(()-> new RuntimeException("Document not found with this id :"+ documentId));

        if (!document.getClaim().getClaimId().equals(claim.getClaimId()))
        {
            throw new RuntimeException(
                    "Document does not belong to this claim");
        }

        String status= request.getVerificationStatus();

        if(!"VERIFIED".equalsIgnoreCase(status) && !"REJECTED".equalsIgnoreCase(status))
        {
            throw new RuntimeException(
                    "Status must be VERIFIED or REJECTED");
        }

        document.setVerificationStatus(status.equalsIgnoreCase("VERIFIED") ? "VERIFIED" : "REJECTED");
        log.info("Document verification completed: claimId={}, documentId={}, status={}", claimId, documentId, document.getVerificationStatus());

        ClaimDocument updateDocument=documentRepository.save(document);

        claimHistoryRepository.save(ClaimHistory.builder()
                        .claim(claim)
                        .action(status.equalsIgnoreCase("VERIFIED") ? "DOCUMENT_VERIFIED" : "DOCUMENT_REJECTED")
                        .performedBy(1L)
                        .remarks("Document verification status: " + document.getVerificationStatus())
                .build());

        return documentMapper.toResponse(updateDocument);

    }
}