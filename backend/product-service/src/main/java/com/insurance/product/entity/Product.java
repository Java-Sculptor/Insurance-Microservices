
package com.insurance.product.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.insurance.product.enums.ProductStatus;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "insurance_products",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_insurance_products_code",
                        columnNames = "product_code"
                ),
                @UniqueConstraint(
                        name = "uk_insurance_products_name",
                        columnNames = "product_name"
                )
        },
        indexes = {
                @Index(name = "idx_products_category", columnList = "category_id"),
                @Index(name = "idx_products_status", columnList = "status")
        }
)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(name = "product_code", nullable = false, length = 30)
    private String productCode;

    @Column(name = "product_name", nullable = false, length = 150)
    private String productName;

    @Column(name = "product_type", length = 50)
    private String productType;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @JsonIgnore
    private ProductCategory category;

    @Column(name = "min_age")
    private Integer minAge;

    @Column(name = "max_age")
    private Integer maxAge;

    @Column(name = "min_sum_insured", precision = 18, scale = 2)
    private BigDecimal minSumInsured;

    @Column(name = "max_sum_insured", precision = 18, scale = 2)
    private BigDecimal maxSumInsured;

    @Column(name = "min_policy_term")
    private Integer minPolicyTerm;

    @Column(name = "max_policy_term")
    private Integer maxPolicyTerm;

    @Column(name = "premium_frequency", length = 30)
    private String premiumFrequency;

    @Column(name = "waiting_period_days")
    private Integer waitingPeriodDays = 0;

    @Column(name = "grace_period_days")
    private Integer gracePeriodDays = 0;

    @Column(name = "gst_percentage", precision = 5, scale = 2)
    private BigDecimal gstPercentage = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ProductStatus status = ProductStatus.DRAFT;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private LocalDateTime updatedAt;

    public Product() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProductCode() {
        return productCode;
    }

    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductType() {
        return productType;
    }

    public void setProductType(String productType) {
        this.productType = productType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ProductCategory getCategory() {
        return category;
    }

    public void setCategory(ProductCategory category) {
        this.category = category;
    }

    public Integer getMinAge() {
        return minAge;
    }

    public void setMinAge(Integer minAge) {
        this.minAge = minAge;
    }

    public Integer getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(Integer maxAge) {
        this.maxAge = maxAge;
    }

    public BigDecimal getMinSumInsured() {
        return minSumInsured;
    }

    public void setMinSumInsured(BigDecimal minSumInsured) {
        this.minSumInsured = minSumInsured;
    }

    public BigDecimal getMaxSumInsured() {
        return maxSumInsured;
    }

    public void setMaxSumInsured(BigDecimal maxSumInsured) {
        this.maxSumInsured = maxSumInsured;
    }

    public Integer getMinPolicyTerm() {
        return minPolicyTerm;
    }

    public void setMinPolicyTerm(Integer minPolicyTerm) {
        this.minPolicyTerm = minPolicyTerm;
    }

    public Integer getMaxPolicyTerm() {
        return maxPolicyTerm;
    }

    public void setMaxPolicyTerm(Integer maxPolicyTerm) {
        this.maxPolicyTerm = maxPolicyTerm;
    }

    public String getPremiumFrequency() {
        return premiumFrequency;
    }

    public void setPremiumFrequency(String premiumFrequency) {
        this.premiumFrequency = premiumFrequency;
    }

    public Integer getWaitingPeriodDays() {
        return waitingPeriodDays;
    }

    public void setWaitingPeriodDays(Integer waitingPeriodDays) {
        this.waitingPeriodDays = waitingPeriodDays;
    }

    public Integer getGracePeriodDays() {
        return gracePeriodDays;
    }

    public void setGracePeriodDays(Integer gracePeriodDays) {
        this.gracePeriodDays = gracePeriodDays;
    }

    public BigDecimal getGstPercentage() {
        return gstPercentage;
    }

    public void setGstPercentage(BigDecimal gstPercentage) {
        this.gstPercentage = gstPercentage;
    }

    public ProductStatus getStatus() {
        return status;
    }

    public void setStatus(ProductStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}
