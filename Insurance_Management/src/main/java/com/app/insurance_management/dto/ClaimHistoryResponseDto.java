
package com.app.insurance_management.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ClaimHistoryResponseDto {

    private Long historyId;
    private Long claimId;
    private String action;
    private Long performedBy;
    private LocalDateTime actionDate;
    private String remarks;
}
