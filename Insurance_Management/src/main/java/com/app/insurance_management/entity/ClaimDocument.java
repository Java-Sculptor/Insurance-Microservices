package com.app.insurance_management.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "claim_documents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClaimDocument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "document_id")
    private Long documentId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "claim_id", nullable = false)
    private ClaimMaster claim;

    @Column(name = "document_name", length = 150)
    private String documentName;

    @Column(name = "file_path", length = 500)
    private String filePath;

    @Column(name = "verification_status", length = 20)
    private String verificationStatus = "PENDING";
}