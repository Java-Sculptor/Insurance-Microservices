
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.CoverageRequest;
import com.insurance.product.dto.CoverageResponse;
import com.insurance.product.service.CoverageService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class CoverageController {

    private final CoverageService service;

    public CoverageController(CoverageService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/products/{id}/coverages")
    public ResponseEntity<ApiResponse<CoverageResponse>> create(
            @PathVariable("id") Long productId,
            @Valid @RequestBody CoverageRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Coverage created successfully",
                        service.create(productId, request)
                )
        );
    }

    @GetMapping("/api/v1/products/{id}/coverages")
    public ResponseEntity<ApiResponse<List<CoverageResponse>>> getAll(
            @PathVariable("id") Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Coverages retrieved successfully",
                        service.getByProduct(productId)
                )
        );
    }

    @PutMapping("/api/v1/coverages/{coverageId}")
    public ResponseEntity<ApiResponse<CoverageResponse>> update(
            @PathVariable Long coverageId,
            @Valid @RequestBody CoverageRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Coverage updated successfully",
                        service.update(coverageId, request)
                )
        );
    }

    @DeleteMapping("/api/v1/coverages/{coverageId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long coverageId) {

        service.delete(coverageId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Coverage deactivated successfully",
                        null
                )
        );
    }
}
