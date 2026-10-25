package com.insurance.quote.controller;

import com.insurance.quote.apiResponse.ApiResponse;
import com.insurance.quote.dto.RiskAssessmentRequestDto;
import com.insurance.quote.dto.RiskAssessmentResponseDto;
import com.insurance.quote.service.RiskAssessmentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/risk-assessment")
public class RiskAssessmentController {

    private final RiskAssessmentService riskAssessmentService;

    public RiskAssessmentController(
            RiskAssessmentService riskAssessmentService) {
        this.riskAssessmentService = riskAssessmentService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<RiskAssessmentResponseDto>>
    assessRisk(@RequestBody RiskAssessmentRequestDto requestDto) {

        RiskAssessmentResponseDto response =
                riskAssessmentService.assessRisk(requestDto);

        return ResponseEntity.ok(
                ApiResponse.<RiskAssessmentResponseDto>builder()
                        .success(true)
                        .message("Risk assessment completed successfully")
                        .data(response)
                        .error(null)
                        .build()
        );
    }

    @GetMapping("/{quoteId}")
    public ResponseEntity<ApiResponse<RiskAssessmentResponseDto>>
    getRiskAssessment(@PathVariable Long quoteId) {

        RiskAssessmentResponseDto response = riskAssessmentService.getRiskAssessment(quoteId);

        return ResponseEntity.ok(
                ApiResponse.<RiskAssessmentResponseDto>builder()
                        .success(true)
                        .message("Risk assessment retrieved successfully")
                        .data(response)
                        .error(null)
                        .build()
        );
    }
}
