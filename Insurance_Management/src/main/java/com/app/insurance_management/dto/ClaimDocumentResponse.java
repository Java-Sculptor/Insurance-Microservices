
package com.app.insurance_management.dto;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimDocumentResponse {

    private Long documentId;
    private Long claimId;
    private String documentName;
    private String filePath;
    private String verificationStatus;
}
