package com.insurance.quote.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class ProductRateRepository {

    private final JdbcTemplate jdbcTemplate;

    public ProductRateRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public java.math.BigDecimal getProductRate(Long productId) {

        return jdbcTemplate.queryForObject(
                "SELECT product_rate FROM insurance_product WHERE product_id = ?",
                java.math.BigDecimal.class,
                productId
        );
    }
}