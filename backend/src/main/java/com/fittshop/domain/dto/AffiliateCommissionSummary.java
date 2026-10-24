package com.fittshop.domain.dto;

import com.fittshop.domain.model.AffiliateCommission;
import java.math.BigDecimal;
import java.util.List;

public class AffiliateCommissionSummary {
    private String affiliateCode;
    private BigDecimal totalEarned;
    private BigDecimal pendingAmount;
    private BigDecimal paidAmount;
    private int totalOrders;
    private List<AffiliateCommission> commissions;

    public AffiliateCommissionSummary() {}

    public AffiliateCommissionSummary(String affiliateCode, BigDecimal totalEarned, BigDecimal pendingAmount,
                                    BigDecimal paidAmount, int totalOrders, List<AffiliateCommission> commissions) {
        this.affiliateCode = affiliateCode;
        this.totalEarned = totalEarned;
        this.pendingAmount = pendingAmount;
        this.paidAmount = paidAmount;
        this.totalOrders = totalOrders;
        this.commissions = commissions;
    }

    public String getAffiliateCode() { return affiliateCode; }
    public void setAffiliateCode(String affiliateCode) { this.affiliateCode = affiliateCode; }

    public BigDecimal getTotalEarned() { return totalEarned; }
    public void setTotalEarned(BigDecimal totalEarned) { this.totalEarned = totalEarned; }

    public BigDecimal getPendingAmount() { return pendingAmount; }
    public void setPendingAmount(BigDecimal pendingAmount) { this.pendingAmount = pendingAmount; }

    public BigDecimal getPaidAmount() { return paidAmount; }
    public void setPaidAmount(BigDecimal paidAmount) { this.paidAmount = paidAmount; }

    public int getTotalOrders() { return totalOrders; }
    public void setTotalOrders(int totalOrders) { this.totalOrders = totalOrders; }

    public List<AffiliateCommission> getCommissions() { return commissions; }
    public void setCommissions(List<AffiliateCommission> commissions) { this.commissions = commissions; }
}
