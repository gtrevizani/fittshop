package com.fittshop.domain.algorithms;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Component
public class SmartDiscountEngine {

    public static class DiscountSummary {
        private final BigDecimal subtotal;
        private final BigDecimal comboDiscount;
        private final BigDecimal couponDiscount;
        private final BigDecimal fittCoinsDiscount;
        private final BigDecimal totalDiscount;
        private final BigDecimal netSubtotal;

        public DiscountSummary(BigDecimal subtotal, BigDecimal comboDiscount, BigDecimal couponDiscount,
                               BigDecimal fittCoinsDiscount, BigDecimal totalDiscount, BigDecimal netSubtotal) {
            this.subtotal = subtotal;
            this.comboDiscount = comboDiscount;
            this.couponDiscount = couponDiscount;
            this.fittCoinsDiscount = fittCoinsDiscount;
            this.totalDiscount = totalDiscount;
            this.netSubtotal = netSubtotal;
        }

        public BigDecimal getSubtotal() { return subtotal; }
        public BigDecimal getComboDiscount() { return comboDiscount; }
        public BigDecimal getCouponDiscount() { return couponDiscount; }
        public BigDecimal getFittCoinsDiscount() { return fittCoinsDiscount; }
        public BigDecimal getTotalDiscount() { return totalDiscount; }
        public BigDecimal getNetSubtotal() { return netSubtotal; }
    }

    public DiscountSummary calculateDiscounts(BigDecimal subtotal, List<String> productNames,
                                              String couponCode, Integer fittCoinsToRedeem,
                                              BigDecimal fittCoinValue, BigDecimal maxFittCoinsDiscountPercent) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return new DiscountSummary(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO,
                    BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
        }

        // 1. Combo Detection (e.g., Whey + Creatina no mesmo pedido ganha 15% de desconto de combo)
        BigDecimal comboDiscount = BigDecimal.ZERO;
        boolean hasWhey = false;
        boolean hasCreatine = false;

        if (productNames != null) {
            for (String name : productNames) {
                if (name == null) continue;
                String lower = name.toLowerCase();
                if (lower.contains("whey") || lower.contains("proteína")) hasWhey = true;
                if (lower.contains("creatina") || lower.contains("creapure")) hasCreatine = true;
            }
        }

        if (hasWhey && hasCreatine) {
            comboDiscount = subtotal.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);
        }

        // 2. Cupom de desconto
        BigDecimal couponDiscount = BigDecimal.ZERO;
        if (couponCode != null) {
            String cleanCoupon = couponCode.trim().toUpperCase();
            if ("FITT10".equals(cleanCoupon)) {
                couponDiscount = subtotal.multiply(new BigDecimal("0.10")).setScale(2, RoundingMode.HALF_UP);
            } else if ("MONSTRO15".equals(cleanCoupon)) {
                couponDiscount = subtotal.multiply(new BigDecimal("0.15")).setScale(2, RoundingMode.HALF_UP);
            } else if ("BEMVINDO20".equals(cleanCoupon)) {
                couponDiscount = new BigDecimal("20.00");
                if (couponDiscount.compareTo(subtotal) > 0) {
                    couponDiscount = subtotal;
                }
            }
        }

        // 3. FittCoins Desconto (Teto de 20% do subtotal para preservar margem da loja)
        BigDecimal fittCoinsDiscount = BigDecimal.ZERO;
        if (fittCoinsToRedeem != null && fittCoinsToRedeem > 0) {
            BigDecimal pointRate = fittCoinValue != null ? fittCoinValue : new BigDecimal("0.50");
            BigDecimal potentialDiscount = BigDecimal.valueOf(fittCoinsToRedeem).multiply(pointRate);

            BigDecimal maxDiscountAllowed = subtotal.multiply(
                    maxFittCoinsDiscountPercent != null ? maxFittCoinsDiscountPercent : new BigDecimal("0.20")
            ).setScale(2, RoundingMode.HALF_UP);

            fittCoinsDiscount = potentialDiscount.min(maxDiscountAllowed);
        }

        BigDecimal totalDiscount = comboDiscount.add(couponDiscount).add(fittCoinsDiscount);
        if (totalDiscount.compareTo(subtotal) > 0) {
            totalDiscount = subtotal;
        }

        BigDecimal netSubtotal = subtotal.subtract(totalDiscount).setScale(2, RoundingMode.HALF_UP);

        return new DiscountSummary(subtotal, comboDiscount, couponDiscount, fittCoinsDiscount, totalDiscount, netSubtotal);
    }
}
