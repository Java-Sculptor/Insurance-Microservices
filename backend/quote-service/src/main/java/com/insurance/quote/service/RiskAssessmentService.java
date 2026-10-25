
package com.insurance.quote.service;


import com.insurance.quote.dto.RiskAssessmentRequestDto;
import com.insurance.quote.dto.RiskAssessmentResponseDto;

public interface RiskAssessmentService {

    RiskAssessmentResponseDto assessRisk(RiskAssessmentRequestDto requestDto);

    RiskAssessmentResponseDto getRiskAssessment(Long quoteId);
}
