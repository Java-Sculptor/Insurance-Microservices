package com.example.customerservice.dto;


import com.example.customerservice.entity.enums.DocumentType;
import com.example.customerservice.entity.enums.VerificationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DocumentResponse {

    private Long documentId;
    private Long customerId;
    private DocumentType documentType;
    private String fileName;
    private LocalDateTime uploadedDate;
}
