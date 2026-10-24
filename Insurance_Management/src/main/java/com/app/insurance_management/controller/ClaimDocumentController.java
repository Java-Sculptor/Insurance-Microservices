package com.app.insurance_management.controller;

import com.app.insurance_management.apiResponse.ApiResponse;
import com.app.insurance_management.dto.ClaimDocumentRequest;
import com.app.insurance_management.dto.ClaimDocumentResponse;
import com.app.insurance_management.dto.DocumentVerificationRequest;
import com.app.insurance_management.service.ClaimDocumentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/claims/{claimId}/documents")
@RequiredArgsConstructor
public class ClaimDocumentController {

    private final ClaimDocumentService documentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<ClaimDocumentResponse>> addDocument(
            @PathVariable Long claimId,
            @RequestParam("documentName") String documentName,
            @RequestParam("file") MultipartFile file) {

        ClaimDocumentRequest request = new ClaimDocumentRequest();
        request.setDocumentName(documentName);

        ClaimDocumentResponse response =
                documentService.addDocument(claimId, request, file);

        ApiResponse<ClaimDocumentResponse> apiResponse =
                ApiResponse.<ClaimDocumentResponse>builder()
                        .success(true)
                        .message("Document added successfully")
                        .data(response)
                        .build();

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<ClaimDocumentResponse>>> getDocuments(
            @PathVariable Long claimId) {

        List<ClaimDocumentResponse> list=documentService.getDocumentsByClaimId(claimId);

        ApiResponse<List<ClaimDocumentResponse>> apiResponse=ApiResponse.<List<ClaimDocumentResponse>>builder()
                .success(true)
                .message("Document are Added Successfully")
                .data(list)
                .build();

        return ResponseEntity.ok(apiResponse);

    }

    @PutMapping("/{documentId}/verify")
    public ResponseEntity<ApiResponse<ClaimDocumentResponse>> verifyDocument(
            @PathVariable Long claimId,
            @PathVariable Long documentId,
            @Valid @RequestBody DocumentVerificationRequest request) {

        ClaimDocumentResponse updateDocument=documentService.verifyDocument(claimId,documentId,request);

        ApiResponse<ClaimDocumentResponse> apiResponse=ApiResponse.<ClaimDocumentResponse>builder()
                .success(true)
                .message("Document are Verified Successfully")
                .data(updateDocument)
                .build();

        return ResponseEntity.ok(apiResponse);
    }
}