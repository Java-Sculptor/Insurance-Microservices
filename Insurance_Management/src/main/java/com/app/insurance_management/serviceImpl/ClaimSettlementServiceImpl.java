
package com.app.insurance_management.serviceImpl;

import com.app.insurance_management.exception.ClaimNotFoundException;
import com.app.insurance_management.dto.SettlementRequestDto;
import com.app.insurance_management.dto.SettlementResponseDto;
import com.app.insurance_management.entity.ClaimAssessment;
import com.app.insurance_management.entity.ClaimHistory;
import com.app.insurance_management.entity.ClaimMaster;
import com.app.insurance_management.entity.ClaimSettlement;
import com.app.insurance_management.enums.ClaimStatus;
import com.app.insurance_management.enums.SettlementStatus;
import com.app.insurance_management.mapper.ClaimSettlementMapper;
import com.app.insurance_management.repository.ClaimAssessmentRepository;
import com.app.insurance_management.repository.ClaimHistoryRepository;
import com.app.insurance_management.repository.ClaimMasterRepository;
import com.app.insurance_management.repository.ClaimSettlementRepository;
import com.app.insurance_management.service.BankAccountVerificationService;
import com.app.insurance_management.service.ClaimSettlementService;
import com.app.insurance_management.service.SettlementAdvicePdfService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClaimSettlementServiceImpl
        implements ClaimSettlementService {

    private final ClaimSettlementRepository settlementRepository;
    private final ClaimMasterRepository claimRepository;
    private final BankAccountVerificationService bankVerificationService;
    private final SettlementAdvicePdfService pdfService;
    private final ClaimSettlementMapper settlementMapper;
    private final ClaimAssessmentRepository assessmentRepository;
    private final ClaimHistoryRepository claimHistoryRepository;

    @Override
    @Transactional
    public SettlementResponseDto createSettlement(
            Long claimId,
            SettlementRequestDto request) {

        // 1. Find claim
        ClaimMaster claim = claimRepository.findById(claimId)
                .orElseThrow(() ->
                        new RuntimeException(
                                "Claim not found with ID: " + claimId));

        // 2. Settlement is allowed only after claim approval
        if (claim.getClaimStatus() == null
                || !claim.getClaimStatus().name().equalsIgnoreCase("APPROVED")) {
            throw new RuntimeException(
                    "Settlement is allowed only for approved claims");
        }

        // 3. Prevent duplicate settlement
        if (settlementRepository.existsByClaim_ClaimId(claimId)) {
            throw new RuntimeException(
                    "Settlement already exists for claim ID: " + claimId);
        }

        boolean verified = bankVerificationService.verifyBankAccount(
                claimId,
                request.getBankAccountNumber(),
                request.getBankIfscCode()
        );

        if (!verified) {
            throw new RuntimeException("Bank account verification failed");
        }

        List<ClaimAssessment> assessments =
                assessmentRepository.findByClaim_ClaimId(claimId);

        if (assessments.isEmpty()) {
            throw new RuntimeException(
                    "Assessment not found for claim ID: " + claimId);
        }

        ClaimAssessment assessment = assessments.get(0);

        BigDecimal approvedAmount = assessment.getNetPayable();

        if (approvedAmount == null
                || approvedAmount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException(
                    "Invalid assessment payable amount");
        }

        // Create settlement
        ClaimSettlement settlement = ClaimSettlement.builder()
                .claim(claim)
                .approvedAmount(approvedAmount)
                .paymentMode(request.getPaymentMode())
                .transactionReference(request.getTransactionReference())
                .settlementDate(LocalDateTime.now())
                .settlementStatus(SettlementStatus.PENDING)
                .remarks(request.getRemarks())
                .build();

        // Save settlement as PENDING
        ClaimSettlement savedSettlement =
                settlementRepository.save(settlement);

        // Generate settlement advice PDF
        String pdfPath =
                pdfService.generateSettlementAdvice(savedSettlement);

        savedSettlement.setSettlementAdvicePath(pdfPath);

        savedSettlement =
                settlementRepository.save(savedSettlement);

        // Maintain claim history
        claimHistoryRepository.save(
                ClaimHistory.builder()
                        .claim(claim)
                        .action("Settlement Initiated")
                        .performedBy(1L)
                        .actionDate(LocalDateTime.now())
                        .remarks(
                                "Settlement initiated. Transaction Reference: "
                                        + savedSettlement.getTransactionReference()
                                        + ", Amount: ₹"
                                        + savedSettlement.getApprovedAmount()
                        )
                        .build()
        );

        // Return settlement response
        return settlementMapper.toResponseDto(savedSettlement);

    }

    @Override
    @Transactional(readOnly = true)
    public SettlementResponseDto getSettlementByClaimId(Long claimId) {

        ClaimSettlement settlement =
                settlementRepository.findByClaim_ClaimId(claimId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Settlement not found for claim ID: "
                                                + claimId));

        return settlementMapper.toResponseDto(settlement);
    }


    @Transactional
    public void markSettlementPaid(Long settlementId) {

        ClaimSettlement settlement =
                settlementRepository.findById(settlementId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Settlement not found: " + settlementId));

        ClaimMaster claim = settlement.getClaim();

        settlement.setSettlementStatus(SettlementStatus.PAID);
        settlementRepository.save(settlement);

        claim.setClaimStatus(ClaimStatus.SETTLED);
        claimRepository.save(claim);

        claimHistoryRepository.save(
                ClaimHistory.builder()
                        .claim(claim)
                        .action("Claim Settled")
                        .performedBy(1L) // Replace with logged-in employee ID
                        .actionDate(LocalDateTime.now())
                        .remarks(
                                "Payment confirmed. Transaction Reference: "
                                        + settlement.getTransactionReference()
                                        + ", Amount: ₹"
                                        + settlement.getApprovedAmount()
                        )
                        .build()
        );
    }

}
