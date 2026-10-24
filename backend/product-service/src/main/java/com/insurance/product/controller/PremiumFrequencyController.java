
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.PremiumFrequencyRequest;
import com.insurance.product.dto.PremiumFrequencyResponse;
import com.insurance.product.service.PremiumFrequencyService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class PremiumFrequencyController {

    private final PremiumFrequencyService service;

    public PremiumFrequencyController(PremiumFrequencyService service) {
        this.service = service;
    }

    // Configure premium frequency for a product
    @PostMapping("/api/v1/products/{productId}/premium-frequencies")
    public ResponseEntity<ApiResponse<PremiumFrequencyResponse>> create(
            @PathVariable Long productId,
            @Valid @RequestBody PremiumFrequencyRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Premium frequency created successfully",
                        service.create(productId, request)
                )
        );
    }

    // Get premium frequencies for a product
    @GetMapping("/api/v1/products/{productId}/premium-frequencies")
    public ApiResponse<List<PremiumFrequencyResponse>> getByProduct(
            @PathVariable Long productId) {

        return ApiResponse.success(
                "Premium frequencies fetched successfully",
                service.getByProduct(productId)
        );
    }

    // Update premium frequency
    @PutMapping("/api/v1/premium-frequencies/{frequencyId}")
    public ApiResponse<PremiumFrequencyResponse> update(
            @PathVariable Long frequencyId,
            @Valid @RequestBody PremiumFrequencyRequest request) {

        return ApiResponse.success(
                "Premium frequency updated successfully",
                service.update(frequencyId, request)
        );
    }

    // Deactivate premium frequency
    @DeleteMapping("/api/v1/premium-frequencies/{frequencyId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long frequencyId) {

        service.delete(frequencyId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Premium frequency deactivated successfully",
                        null
                )
        );
    }
}
