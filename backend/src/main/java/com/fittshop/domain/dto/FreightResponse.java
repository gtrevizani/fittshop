package com.fittshop.domain.dto;

import java.math.BigDecimal;

public class FreightResponse {
    private BigDecimal freightAmount;
    private int estimatedDeliveryDays;
    private boolean freeShipping;
    private String carrierName;

    public FreightResponse() {}

    public FreightResponse(BigDecimal freightAmount, int estimatedDeliveryDays, boolean freeShipping, String carrierName) {
        this.freightAmount = freightAmount;
        this.estimatedDeliveryDays = estimatedDeliveryDays;
        this.freeShipping = freeShipping;
        this.carrierName = carrierName;
    }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public int getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
    public void setEstimatedDeliveryDays(int estimatedDeliveryDays) { this.estimatedDeliveryDays = estimatedDeliveryDays; }

    public boolean isFreeShipping() { return freeShipping; }
    public void setFreeShipping(boolean freeShipping) { this.freeShipping = freeShipping; }

    public String getCarrierName() { return carrierName; }
    public void setCarrierName(String carrierName) { this.carrierName = carrierName; }
}
