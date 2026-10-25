package com.insurance.quote.serviceImpl;

import com.insurance.quote.dto.RiskAssessmentRequestDto;
import com.insurance.quote.dto.RiskAssessmentResponseDto;
import com.insurance.quote.entity.Quote;
import com.insurance.quote.entity.RiskAssessment;
import com.insurance.quote.enums.RiskCategory;
import com.insurance.quote.repository.QuoteRepository;
import com.insurance.quote.repository.RiskAssessmentRepository;
import com.insurance.quote.service.RiskAssessmentService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RiskAssessmentServiceImpl
        implements RiskAssessmentService {

    private final RiskAssessmentRepository riskAssessmentRepository;
    private final QuoteRepository quoteRepository;

    public RiskAssessmentServiceImpl(
            RiskAssessmentRepository riskAssessmentRepository,
            QuoteRepository quoteRepository) {

        this.riskAssessmentRepository = riskAssessmentRepository;
        this.quoteRepository = quoteRepository;
    }

    @Override
    @Transactional
    public RiskAssessmentResponseDto assessRisk(
            RiskAssessmentRequestDto requestDto) {

        // 1. Validate quote ID
        if (requestDto.getQuoteId() == null) {
            throw new RuntimeException("Quote ID is required");
        }

        // 2. Fetch quote
        Quote quote = quoteRepository.findById(requestDto.getQuoteId())
                .orElseThrow(() ->
                        new RuntimeException("Quote not found"));

        // 3. Validate risk scores
        validateScore(requestDto.getAgeScore(), "Age score");
        validateScore(requestDto.getOccupationScore(), "Occupation score");
        validateScore(requestDto.getMedicalScore(), "Medical score");
        validateScore(requestDto.getLifestyleScore(), "Lifestyle score");

        // 4. Calculate total score
        int totalScore =
                requestDto.getAgeScore()
                        + requestDto.getOccupationScore()
                        + requestDto.getMedicalScore()
                        + requestDto.getLifestyleScore();

        if (totalScore > 100) {
            throw new RuntimeException(
                    "Total risk score cannot exceed 100");
        }

        // 5. Determine risk category
        RiskCategory riskCategory;

        if (totalScore <= 30) {
            riskCategory = RiskCategory.Low;
        } else if (totalScore <= 70) {
            riskCategory = RiskCategory.Medium;
        } else {
            riskCategory = RiskCategory.High;
        }

        // 6. Find existing assessment or create a new one
        RiskAssessment assessment =
                riskAssessmentRepository
                        .findByQuote_QuoteId(quote.getQuoteId())
                        .orElseGet(RiskAssessment::new);

        // 7. Set assessment details
        assessment.setQuote(quote);
        assessment.setAgeScore(requestDto.getAgeScore());
        assessment.setOccupationScore(
                requestDto.getOccupationScore());
        assessment.setMedicalScore(requestDto.getMedicalScore());
        assessment.setLifestyleScore(requestDto.getLifestyleScore());
        assessment.setTotalScore(totalScore);
        assessment.setRiskCategory(riskCategory);

        // 8. Save assessment
        RiskAssessment saved =
                riskAssessmentRepository.save(assessment);

        // 9. Convert entity to response DTO
        return mapToResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public RiskAssessmentResponseDto getRiskAssessment(Long quoteId) {

        RiskAssessment assessment =
                riskAssessmentRepository
                        .findByQuote_QuoteId(quoteId)
                        .orElseThrow(() ->
                                new RuntimeException(
                                        "Risk assessment not found for quote: "
                                                + quoteId));

        return mapToResponse(assessment);
    }

    private void validateScore(Integer score, String fieldName) {
        if (score == null) {
            throw new RuntimeException(fieldName + " is required");
        }

        if (score < 0 || score > 100) {
            throw new RuntimeException(
                    fieldName + " must be between 0 and 100");
        }
    }

    private RiskAssessmentResponseDto mapToResponse(
            RiskAssessment assessment) {

        RiskAssessmentResponseDto response =
                new RiskAssessmentResponseDto();

        response.setRiskId(assessment.getRiskId());
        response.setQuoteId(
                assessment.getQuote().getQuoteId());
        response.setAgeScore(assessment.getAgeScore());
        response.setOccupationScore(
                assessment.getOccupationScore());
        response.setMedicalScore(assessment.getMedicalScore());
        response.setLifestyleScore(
                assessment.getLifestyleScore());
        response.setTotalScore(assessment.getTotalScore());
        response.setRiskCategory(assessment.getRiskCategory());

        return response;
    }
}
