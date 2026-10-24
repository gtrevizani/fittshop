package com.fittshop.domain.dto;

import java.math.BigDecimal;

public class AffiliateValidateResponse {
    private boolean valid;
    private String affiliateCode;
    private String affiliateName;
    private BigDecimal commissionRate;
    private BigDecimal discountPercentage;

    public AffiliateValidateResponse() {}

    public AffiliateValidateResponse(boolean valid, String affiliateCode, String affiliateName,
                                     BigDecimal commissionRate, BigDecimal discountPercentage) {
        this.valid = valid;
        this.affiliateCode = affiliateCode;
        this.affiliateName = affiliateName;
        this.commissionRate = commissionRate;
        this.discountPercentage = discountPercentage;
    }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public String getAffiliateCode() { return affiliateCode; }
    public void setAffiliateCode(String affiliateCode) { this.affiliateCode = affiliateCode; }

    public String getAffiliateName() { return affiliateName; }
    public void setAffiliateName(String affiliateName) { this.affiliateName = affiliateName; }

    public BigDecimal getCommissionRate() { return commissionRate; }
    public void setCommissionRate(BigDecimal commissionRate) { this.commissionRate = commissionRate; }

    public BigDecimal getDiscountPercentage() { return discountPercentage; }
    public void setDiscountPercentage(BigDecimal discountPercentage) { this.discountPercentage = discountPercentage; }
}
