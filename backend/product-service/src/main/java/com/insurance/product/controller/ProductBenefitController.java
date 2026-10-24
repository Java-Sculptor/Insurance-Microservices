
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.BenefitRequest;
import com.insurance.product.dto.BenefitResponse;
import com.insurance.product.service.ProductBenefitService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class ProductBenefitController {

    private final ProductBenefitService service;

    public ProductBenefitController(ProductBenefitService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/products/{productId}/benefits")
    public ResponseEntity<ApiResponse<BenefitResponse>> create(
            @PathVariable Long productId,
            @Valid @RequestBody BenefitRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Benefit created successfully",
                        service.create(productId, request)
                )
        );
    }

    @GetMapping("/api/v1/products/{productId}/benefits")
    public ResponseEntity<ApiResponse<List<BenefitResponse>>> getAll(
            @PathVariable Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Benefits retrieved successfully",
                        service.getByProduct(productId)
                )
        );
    }

    @PutMapping("/api/v1/benefits/{benefitId}")
    public ResponseEntity<ApiResponse<BenefitResponse>> update(
            @PathVariable Long benefitId,
            @Valid @RequestBody BenefitRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Benefit updated successfully",
                        service.update(benefitId, request)
                )
        );
    }

    @DeleteMapping("/api/v1/benefits/{benefitId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long benefitId) {

        service.delete(benefitId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Benefit deactivated successfully",
                        null
                )
        );
    }
}
