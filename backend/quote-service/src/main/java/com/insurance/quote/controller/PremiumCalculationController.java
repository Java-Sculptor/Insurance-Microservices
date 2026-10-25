package com.insurance.quote.controller;


import com.insurance.quote.apiResponse.ApiResponse;
import com.insurance.quote.dto.PremiumCalculationRequestDto;
import com.insurance.quote.dto.PremiumCalculationResponseDto;
import com.insurance.quote.service.PremiumCalculationService;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/quotes")
public class PremiumCalculationController {

    private final PremiumCalculationService premiumCalculationService;

    public PremiumCalculationController(
            PremiumCalculationService premiumCalculationService) {
        this.premiumCalculationService = premiumCalculationService;
    }

    // Calculate premium for a quote
    @PostMapping("/calculate-premium")
    public ResponseEntity<ApiResponse<PremiumCalculationResponseDto>>
    calculatePremium(
            @RequestBody PremiumCalculationRequestDto requestDto) {

       PremiumCalculationResponseDto response = premiumCalculationService.calculatePremium(requestDto);

        ApiResponse<PremiumCalculationResponseDto> apiResponse =
                new ApiResponse<>(
                        true,
                        "Premium calculated successfully",
                        response,
                        null
                );

        return ResponseEntity.ok(apiResponse);
    }

    // Get calculated premium using quote ID
    @GetMapping("/{id}/premium")
    public ResponseEntity<ApiResponse<PremiumCalculationResponseDto>>
    getPremiumByQuoteId(@PathVariable Long id) {

        PremiumCalculationResponseDto response =
                premiumCalculationService.getPremiumByQuoteId(id);

        ApiResponse<PremiumCalculationResponseDto> apiResponse =
                new ApiResponse<>(
                        true,
                        "Premium details fetched successfully",
                        response,
                        null
                );

        return ResponseEntity.ok(apiResponse);
    }
}
