package com.insurance.product.dto;

public record CategoryResponse(
        Long id,
        String categoryName,
        String description,
        boolean active
) {
}