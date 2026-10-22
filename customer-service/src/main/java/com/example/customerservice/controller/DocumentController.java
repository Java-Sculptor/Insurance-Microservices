package com.example.customerservice.controller;
import com.example.customerservice.dto.DocumentResponse;
import com.example.customerservice.entity.enums.DocumentType;
import com.example.customerservice.service.DocumentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    private final DocumentService documentService;

    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    // Upload document
    @PostMapping(
            value = "/customers/{id}/documents",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<DocumentResponse> uploadDocument(
            @PathVariable Long id,
            @RequestParam("documentType") DocumentType documentType,
            @RequestParam("file") MultipartFile file) {

        DocumentResponse response =
                documentService.uploadDocument(id, documentType, file);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Get all documents for a customer
    @GetMapping("/customers/{id}/documents")
    public ResponseEntity<List<DocumentResponse>> getDocuments(
            @PathVariable Long id) {

        List<DocumentResponse> documents =
                documentService.getDocumentsByCustomerId(id);

        return ResponseEntity.ok(documents);
    }

    // Delete a document
    @DeleteMapping("/documents/{documentId}")
    public ResponseEntity<Void> deleteDocument(
            @PathVariable Long documentId) {

        documentService.deleteDocument(documentId);

        return ResponseEntity.noContent().build();
    }
}
