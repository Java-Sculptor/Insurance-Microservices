package com.example.customerservice.service;


import com.example.customerservice.dto.DocumentResponse;
import com.example.customerservice.entity.enums.DocumentType;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface DocumentService {

    DocumentResponse uploadDocument(
            Long customerId,
            DocumentType documentType,
            MultipartFile file);

    List<DocumentResponse> getDocumentsByCustomerId(Long customerId);

    void deleteDocument(Long documentId);

}
