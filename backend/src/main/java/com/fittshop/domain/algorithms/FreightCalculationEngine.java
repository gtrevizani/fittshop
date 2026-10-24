package com.fittshop.domain.algorithms;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class FreightCalculationEngine {

    public static class FreightResult {
        private final BigDecimal freightAmount;
        private final int estimatedDeliveryDays;
        private final boolean freeShipping;
        private final String carrierName;

        public FreightResult(BigDecimal freightAmount, int estimatedDeliveryDays, boolean freeShipping, String carrierName) {
            this.freightAmount = freightAmount;
            this.estimatedDeliveryDays = estimatedDeliveryDays;
            this.freeShipping = freeShipping;
            this.carrierName = carrierName;
        }

        public BigDecimal getFreightAmount() { return freightAmount; }
        public int getEstimatedDeliveryDays() { return estimatedDeliveryDays; }
        public boolean isFreeShipping() { return freeShipping; }
        public String getCarrierName() { return carrierName; }
    }

    public FreightResult calculate(String zipCode, BigDecimal subtotal, double totalWeightKg,
                                  BigDecimal thresholdSudeste, BigDecimal thresholdOutros) {
        String cleanZip = (zipCode != null) ? zipCode.replaceAll("\\D", "") : "01310100";
        if (cleanZip.length() < 5) cleanZip = "01310100";

        int prefix = Integer.parseInt(cleanZip.substring(0, Math.min(5, cleanZip.length())));
        double weight = Math.max(totalWeightKg, 0.5);

        BigDecimal basePrice;
        BigDecimal weightRate;
        int deliveryDays;
        boolean isSudeste = (prefix >= 1000 && prefix <= 39999);
        boolean isSul = (prefix >= 80000 && prefix <= 99999);

        BigDecimal freeThreshold = isSudeste || isSul
                ? (thresholdSudeste != null ? thresholdSudeste : new BigDecimal("199.00"))
                : (thresholdOutros != null ? thresholdOutros : new BigDecimal("299.00"));

        if (subtotal != null && subtotal.compareTo(freeThreshold) >= 0) {
            deliveryDays = isSudeste ? 2 : (isSul ? 3 : 6);
            return new FreightResult(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), deliveryDays, true, "FittExpress (Frete Grátis Atleta)");
        }

        if (isSudeste) {
            basePrice = new BigDecimal("14.90");
            weightRate = new BigDecimal("1.50");
            deliveryDays = 2;
        } else if (isSul) {
            basePrice = new BigDecimal("18.90");
            weightRate = new BigDecimal("1.80");
            deliveryDays = 3;
        } else {
            basePrice = new BigDecimal("29.90");
            weightRate = new BigDecimal("2.50");
            deliveryDays = 6;
        }

        BigDecimal extraWeight = BigDecimal.valueOf(Math.max(0, weight - 1.0));
        BigDecimal finalFreight = basePrice.add(extraWeight.multiply(weightRate)).setScale(2, RoundingMode.HALF_UP);

        return new FreightResult(finalFreight, deliveryDays, false, "FittLog Transportes");
    }
}
