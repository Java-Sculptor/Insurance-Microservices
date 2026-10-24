
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.CategoryRequest;
import com.insurance.product.dto.CategoryResponse;
import com.insurance.product.service.ProductCategoryService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/product-categories")
public class ProductCategoryController {

    private final ProductCategoryService service;

    public ProductCategoryController(ProductCategoryService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CategoryResponse>> create(
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success(
                        "Category created successfully",
                        service.create(request)
                )
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CategoryResponse>>> getAll() {
        return ResponseEntity.ok(
                ApiResponse.success(
                        "Categories fetched successfully",
                        service.getAll()
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category fetched successfully",
                        service.getById(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CategoryResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category updated successfully",
                        service.update(id, request)
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<CategoryResponse>> changeStatus(
            @PathVariable Long id,
            @RequestBody Map<String, Boolean> body) {

        Boolean active = body.get("active");

        if (active == null) {
            return ResponseEntity.badRequest().body(
                    ApiResponse.success(
                            "The active field is required",
                            null
                    )
            );
        }

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category status updated successfully",
                        service.changeStatus(id, active)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable Long id) {

        service.deactivate(id);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Category deactivated successfully",
                        null
                )
        );
    }
}
