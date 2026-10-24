
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.CommissionConfigurationRequest;
import com.insurance.product.dto.CommissionConfigurationResponse;
import com.insurance.product.service.CommissionConfigurationService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/product-categories/{categoryId}/commission")
public class CommissionConfigurationController {

    private final CommissionConfigurationService service;

    public CommissionConfigurationController(
            CommissionConfigurationService service) {
        this.service = service;
    }

    // Create commission configuration
    @PostMapping
    public ResponseEntity<ApiResponse<CommissionConfigurationResponse>> create(
            @PathVariable Long categoryId,
            @Valid @RequestBody CommissionConfigurationRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Commission configuration created",
                        service.create(categoryId, request)
                )
        );
    }

    // Get commission configuration
    @GetMapping
    public ApiResponse<CommissionConfigurationResponse> getByCategory(
            @PathVariable Long categoryId) {

        return ApiResponse.success(
                "Commission configuration fetched",
                service.getByCategory(categoryId)
        );
    }

    // Update commission configuration
    @PutMapping
    public ApiResponse<CommissionConfigurationResponse> update(
            @PathVariable Long categoryId,
            @Valid @RequestBody CommissionConfigurationRequest request) {

        return ApiResponse.success(
                "Commission configuration updated",
                service.update(categoryId, request)
        );
    }

    // Delete commission configuration
    @DeleteMapping
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long categoryId) {

        service.delete(categoryId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Commission configuration deleted", null)
        );
    }
}
