package com.fittshop.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "affiliate_commissions")
public class AffiliateCommission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String affiliateCode;

    @Column(nullable = false)
    private Long orderId;

    @Column(nullable = false, length = 64)
    private String orderNumber;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderTotal;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal commissionRate;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal commissionAmount;

    @Column(nullable = false, length = 30)
    private String status; // PENDING, APPROVED, PAID

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime paidAt;

    public AffiliateCommission() {}

    public AffiliateCommission(String affiliateCode, Long orderId, String orderNumber, BigDecimal orderTotal,
                               BigDecimal commissionRate, BigDecimal commissionAmount, String status) {
        this.affiliateCode = affiliateCode;
        this.orderId = orderId;
        this.orderNumber = orderNumber;
        this.orderTotal = orderTotal;
        this.commissionRate = commissionRate;
        this.commissionAmount = commissionAmount;
        this.status = status;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getAffiliateCode() { return affiliateCode; }
    public void setAffiliateCode(String affiliateCode) { this.affiliateCode = affiliateCode; }

    public Long getOrderId() { return orderId; }
    public void setOrderId(Long orderId) { this.orderId = orderId; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public BigDecimal getOrderTotal() { return orderTotal; }
    public void setOrderTotal(BigDecimal orderTotal) { this.orderTotal = orderTotal; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getCommissionAmount() { return commissionAmount; }
    public void setCommissionAmount(BigDecimal commissionAmount) { this.commissionAmount = commissionAmount; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getPaidAt() { return paidAt; }
    public void setPaidAt(LocalDateTime paidAt) { this.paidAt = paidAt; }
}
