
package com.insurance.product.controller;

import com.insurance.product.apiResponse.ApiResponse;
import com.insurance.product.dto.ProductRequest;
import com.insurance.product.dto.ProductResponse;
import com.insurance.product.enums.ProductStatus;
import com.insurance.product.service.ProductService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<ProductResponse>> create(
            @Valid @RequestBody ProductRequest request) {

        ProductResponse response = productService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED).body(
                ApiResponse.success("Product created successfully", response)
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<Page<ProductResponse>>> getAll(
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) ProductStatus status,
            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "id",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        Page<ProductResponse> response =
                productService.getAll(categoryId, status, pageable);

        return ResponseEntity.ok(
                ApiResponse.success("Products retrieved successfully", response)
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> getById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product retrieved successfully",
                        productService.getById(id)
                )
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ProductResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product updated successfully",
                        productService.update(id, request)
                )
        );
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<ApiResponse<ProductResponse>> changeStatus(
            @PathVariable Long id,
            @RequestParam ProductStatus status) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product status updated successfully",
                        productService.changeStatus(id, status)
                )
        );
    }

    @PostMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<ProductResponse>> publish(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Product published successfully",
                        productService.publish(id)
                )
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deactivate(
            @PathVariable Long id) {

        productService.deactivate(id);

        return ResponseEntity.ok(
                ApiResponse.success("Product deactivated successfully", null)
        );
    }
}
