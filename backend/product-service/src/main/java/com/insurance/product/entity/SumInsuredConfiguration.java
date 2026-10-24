
package com.insurance.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "sum_insured_configurations")
public class SumInsuredConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sum_insured_configuration_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "minimum_sum_insured", nullable = false,
            precision = 18, scale = 2)
    private BigDecimal minimumSumInsured;

    @Column(name = "maximum_sum_insured", nullable = false,
            precision = 18, scale = 2)
    private BigDecimal maximumSumInsured;

    @Column(name = "default_sum_insured", precision = 18, scale = 2)
    private BigDecimal defaultSumInsured;

    @Column(nullable = false)
    private boolean active = true;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public BigDecimal getMinimumSumInsured() {
        return minimumSumInsured;
    }

    public void setMinimumSumInsured(BigDecimal minimumSumInsured) {
        this.minimumSumInsured = minimumSumInsured;
    }

    public BigDecimal getMaximumSumInsured() {
        return maximumSumInsured;
    }

    public void setMaximumSumInsured(BigDecimal maximumSumInsured) {
        this.maximumSumInsured = maximumSumInsured;
    }

    public BigDecimal getDefaultSumInsured() {
        return defaultSumInsured;
    }

    public void setDefaultSumInsured(BigDecimal defaultSumInsured) {
        this.defaultSumInsured = defaultSumInsured;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
