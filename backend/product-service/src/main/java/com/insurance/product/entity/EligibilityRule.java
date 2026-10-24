
package com.insurance.product.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "eligibility_rules",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_eligibility_rules_product",
                        columnNames = "product_id"
                )
        }
)
public class EligibilityRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "eligibility_rule_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;

    @Column(name = "minimum_age", nullable = false)
    private Integer minimumAge;

    @Column(name = "maximum_age", nullable = false)
    private Integer maximumAge;

    @Column(name = "minimum_income", nullable = false, precision = 18, scale = 2)
    private BigDecimal minimumIncome;

    @Column(name = "occupation_category", nullable = false, length = 100)
    private String occupationCategory;

    @Column(name = "medical_required", nullable = false)
    private boolean medicalRequired;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();

        if (createdAt == null) {
            createdAt = now;
        }

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

    public Integer getMinimumAge() {
        return minimumAge;
    }

    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }

    public Integer getMaximumAge() {
        return maximumAge;
    }

    public void setMaximumAge(Integer maximumAge) {
        this.maximumAge = maximumAge;
    }

    public BigDecimal getMinimumIncome() {
        return minimumIncome;
    }

    public void setMinimumIncome(BigDecimal minimumIncome) {
        this.minimumIncome = minimumIncome;
    }

    public String getOccupationCategory() {
        return occupationCategory;
    }

    public void setOccupationCategory(String occupationCategory) {
        this.occupationCategory = occupationCategory;
    }

    public boolean isMedicalRequired() {
        return medicalRequired;
    }

    public void setMedicalRequired(boolean medicalRequired) {
        this.medicalRequired = medicalRequired;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
