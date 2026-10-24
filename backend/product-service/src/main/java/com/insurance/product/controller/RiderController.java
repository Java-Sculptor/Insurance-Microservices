
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.RiderRequest;
import com.insurance.product.dto.RiderResponse;
import com.insurance.product.service.RiderService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class RiderController {

    private final RiderService service;

    public RiderController(RiderService service) {
        this.service = service;
    }

    @PostMapping("/api/v1/products/{id}/riders")
    public ResponseEntity<ApiResponse<RiderResponse>> create(
            @PathVariable("id") Long productId,
            @Valid @RequestBody RiderRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Rider created successfully",
                        service.create(productId, request)
                )
        );
    }

    @GetMapping("/api/v1/products/{id}/riders")
    public ResponseEntity<ApiResponse<List<RiderResponse>>> getByProduct(
            @PathVariable("id") Long productId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Riders retrieved successfully",
                        service.getByProduct(productId)
                )
        );
    }

    @GetMapping("/api/v1/riders/{riderId}")
    public ResponseEntity<ApiResponse<RiderResponse>> getById(
            @PathVariable Long riderId) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Rider retrieved successfully",
                        service.getById(riderId)
                )
        );
    }

    @PutMapping("/api/v1/riders/{riderId}")
    public ResponseEntity<ApiResponse<RiderResponse>> update(
            @PathVariable Long riderId,
            @Valid @RequestBody RiderRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Rider updated successfully",
                        service.update(riderId, request)
                )
        );
    }

    @DeleteMapping("/api/v1/riders/{riderId}")
    public ResponseEntity<ApiResponse<Void>> delete(
            @PathVariable Long riderId) {

        service.delete(riderId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Rider deactivated successfully",
                        null
                )
        );
    }
}
