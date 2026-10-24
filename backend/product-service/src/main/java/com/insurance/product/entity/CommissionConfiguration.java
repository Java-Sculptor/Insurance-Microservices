
package com.insurance.product.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "commission_configurations",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_commission_product_category",
                        columnNames = {"product_category_id"}
                )
        }
)
public class CommissionConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "commission_configuration_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "product_category_id",
            nullable = false,
            unique = true
    )
    private ProductCategory productCategory;

    @Column(name = "agent_commission", nullable = false,
            precision = 5, scale = 2)
    private BigDecimal agentCommission;

    @Column(name = "branch_commission", nullable = false,
            precision = 5, scale = 2)
    private BigDecimal branchCommission;

    @Column(name = "renewal_commission", nullable = false,
            precision = 5, scale = 2)
    private BigDecimal renewalCommission;

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

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public BigDecimal getAgentCommission() {
        return agentCommission;
    }

    public void setAgentCommission(BigDecimal agentCommission) {
        this.agentCommission = agentCommission;
    }

    public BigDecimal getBranchCommission() {
        return branchCommission;
    }

    public void setBranchCommission(BigDecimal branchCommission) {
        this.branchCommission = branchCommission;
    }

    public BigDecimal getRenewalCommission() {
        return renewalCommission;
    }

    public void setRenewalCommission(BigDecimal renewalCommission) {
        this.renewalCommission = renewalCommission;
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
