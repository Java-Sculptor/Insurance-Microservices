
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.EligibilityRuleRequest;
import com.insurance.product.dto.EligibilityRuleResponse;
import com.insurance.product.service.EligibilityRuleService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products/{productId}/eligibility-rules")
public class EligibilityRuleController {

    private final EligibilityRuleService service;

    public EligibilityRuleController(EligibilityRuleService service) {
        this.service = service;
    }

    // Create eligibility rules
    @PostMapping
    public ResponseEntity<ApiResponse<EligibilityRuleResponse>> create(
            @PathVariable Long productId,
            @Valid @RequestBody EligibilityRuleRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Eligibility rules created successfully",
                        service.create(productId, request)
                )
        );
    }

    // Get eligibility rules for a product
    @GetMapping
    public ApiResponse<EligibilityRuleResponse> getByProduct(
            @PathVariable Long productId) {

        return ApiResponse.success(
                "Eligibility rules fetched successfully",
                service.getByProduct(productId)
        );
    }

    // Update eligibility rules
    @PutMapping
    public ApiResponse<EligibilityRuleResponse> update(
            @PathVariable Long productId,
            @Valid @RequestBody EligibilityRuleRequest request) {

        return ApiResponse.success(
                "Eligibility rules updated successfully",
                service.update(productId, request)
        );
    }

    // Delete eligibility rules
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long productId) {

        service.delete(productId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Eligibility rules deleted successfully",
                        null
                )
        );
    }
}
