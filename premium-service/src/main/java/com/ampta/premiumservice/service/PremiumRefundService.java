package com.ampta.premiumservice.service;

import com.ampta.premiumservice.dto.RefundApprovalDto;
import com.ampta.premiumservice.dto.RefundRequestDto;
import com.ampta.premiumservice.dto.RefundResponseDto;

import java.util.List;

public interface PremiumRefundService {
    RefundResponseDto createRefund(RefundRequestDto request);
    RefundResponseDto approveRefund(Long refundId, RefundApprovalDto approvalDto);
    RefundResponseDto getRefund(Long refundId);
    List<RefundResponseDto> getRefundsByPolicy(Long policyId);
}
