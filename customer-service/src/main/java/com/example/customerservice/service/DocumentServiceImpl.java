package com.example.customerservice.service;


import com.example.customerservice.dto.DocumentResponse;
import com.example.customerservice.entity.Customer;
import com.example.customerservice.entity.CustomerDocument;
import com.example.customerservice.entity.enums.DocumentType;
import com.example.customerservice.entity.enums.VerificationStatus;
import com.example.customerservice.exception.CustomerNotFoundException;
import com.example.customerservice.exception.ResourceNotFoundException;
import com.example.customerservice.repository.CustomerDocumentRepository;
import com.example.customerservice.repository.CustomerRepository;
import com.example.customerservice.service.DocumentService;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Service
public class DocumentServiceImpl implements DocumentService {

    private final CustomerDocumentRepository documentRepository;
    private final CustomerRepository customerRepository;
    private final Path uploadDirectory;

    public DocumentServiceImpl(
            CustomerDocumentRepository documentRepository,
            CustomerRepository customerRepository,
            @Value("${app.document.upload-dir:uploads/documents}") String uploadDir) {

        this.documentRepository = documentRepository;
        this.customerRepository = customerRepository;
        this.uploadDirectory = Paths.get(uploadDir)
                .toAbsolutePath()
                .normalize();

        try {
            Files.createDirectories(uploadDirectory);
        } catch (IOException e) {
            throw new RuntimeException("Could not create document upload directory", e);
        }
    }

    @Override
    @Transactional
    public DocumentResponse uploadDocument(
            Long customerId,
            DocumentType documentType,
            MultipartFile file) {

        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(
                        "Customer not found with ID: " + customerId));

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Please select a file to upload");
        }

        String originalFileName = file.getOriginalFilename();

        if (originalFileName == null || originalFileName.isBlank()) {
            throw new IllegalArgumentException("Invalid file name");
        }

        // Use a generated filename to avoid overwriting files with the same name.
        String safeFileName = UUID.randomUUID() + "_" +
                Paths.get(originalFileName).getFileName();

        Path targetPath = uploadDirectory.resolve(safeFileName).normalize();

        if (!targetPath.startsWith(uploadDirectory)) {
            throw new IllegalArgumentException("Invalid file path");
        }

        try {
            Files.copy(file.getInputStream(), targetPath,
                    StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new RuntimeException("Failed to store uploaded document", e);
        }

        CustomerDocument document = new CustomerDocument();
        document.setCustomer(customer);
        document.setDocumentType(documentType);
        document.setFileName(originalFileName);
        document.setFilePath(targetPath.toString());

        try {
            CustomerDocument savedDocument = documentRepository.save(document);
            return mapToResponse(savedDocument);
        } catch (RuntimeException e) {
            try {
                Files.deleteIfExists(targetPath);
            } catch (IOException ignored) {
                // Keep the original database error.
            }
            throw e;
        }
    }

    @Override
    public List<DocumentResponse> getDocumentsByCustomerId(Long customerId) {

        if (!customerRepository.existsById(customerId)) {
            throw new CustomerNotFoundException(
                    "Customer not found with ID: " + customerId);
        }

        return documentRepository.findByCustomerCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    @Override
    @Transactional
    public void deleteDocument(Long documentId) {

        CustomerDocument document = documentRepository.findById(documentId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Document not found with ID: " + documentId));

        Path filePath = Paths.get(document.getFilePath()).toAbsolutePath().normalize();

        documentRepository.delete(document);

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            throw new RuntimeException("Document record deleted, but file deletion failed", e);
        }
    }



    private DocumentResponse mapToResponse(CustomerDocument document) {

        DocumentResponse response = new DocumentResponse();

        response.setDocumentId(document.getDocumentId());
        response.setCustomerId(document.getCustomer().getCustomerId());
        response.setDocumentType(document.getDocumentType());
        response.setFileName(document.getFileName());
        response.setUploadedDate(document.getUploadedDate());

        return response;
    }
}

