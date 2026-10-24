
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.SumInsuredConfigurationRequest;
import com.insurance.product.dto.SumInsuredConfigurationResponse;
import com.insurance.product.service.SumInsuredConfigurationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products/{productId}/sum-insured")
public class SumInsuredConfigurationController {

    private final SumInsuredConfigurationService service;

    public SumInsuredConfigurationController(
            SumInsuredConfigurationService service) {
        this.service = service;
    }

    // Create sum insured configuration
    @PostMapping
    public ResponseEntity<ApiResponse<SumInsuredConfigurationResponse>> create(
            @PathVariable Long productId,
            @Valid @RequestBody SumInsuredConfigurationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Sum insured configuration created",
                        service.create(productId, request)
                )
        );
    }

    // Get sum insured configurations for a product
    @GetMapping
    public ApiResponse<List<SumInsuredConfigurationResponse>> getByProduct(
            @PathVariable Long productId) {

        return ApiResponse.success(
                "Sum insured configurations fetched",
                service.getByProduct(productId)
        );
    }

    // Update a specific configuration
    @PutMapping("/{configurationId}")
    public ApiResponse<SumInsuredConfigurationResponse> update(
            @PathVariable Long productId,
            @PathVariable Long configurationId,
            @Valid @RequestBody SumInsuredConfigurationRequest request) {

        return ApiResponse.success(
                "Sum insured configuration updated",
                service.update(productId, configurationId, request)
        );
    }

    // Delete a specific configuration
    @DeleteMapping("/{configurationId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long productId,
            @PathVariable Long configurationId) {

        service.delete(productId, configurationId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Sum insured configuration deleted", null)
        );
    }
}
