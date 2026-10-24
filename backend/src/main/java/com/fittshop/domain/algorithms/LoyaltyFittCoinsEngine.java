package com.fittshop.domain.algorithms;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Component
public class LoyaltyFittCoinsEngine {

    public static class LoyaltyCalculationResult {
        private final int pointsEarned;
        private final BigDecimal cashValueEarned;
        private final int maxRedeemablePoints;
        private final BigDecimal maxRedeemableCashValue;

        public LoyaltyCalculationResult(int pointsEarned, BigDecimal cashValueEarned,
                                        int maxRedeemablePoints, BigDecimal maxRedeemableCashValue) {
            this.pointsEarned = pointsEarned;
            this.cashValueEarned = cashValueEarned;
            this.maxRedeemablePoints = maxRedeemablePoints;
            this.maxRedeemableCashValue = maxRedeemableCashValue;
        }

        public int getPointsEarned() { return pointsEarned; }
        public BigDecimal getCashValueEarned() { return cashValueEarned; }
        public int getMaxRedeemablePoints() { return maxRedeemablePoints; }
        public BigDecimal getMaxRedeemableCashValue() { return maxRedeemableCashValue; }
    }

    public LoyaltyCalculationResult evaluate(BigDecimal subtotal, int currentAccountBalance,
                                             double earnRatePerReal, BigDecimal coinCashValue,
                                             BigDecimal maxDiscountPercentage) {
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return new LoyaltyCalculationResult(0, BigDecimal.ZERO, 0, BigDecimal.ZERO);
        }

        // 1 FittCoin para cada R$ 10,00 gastos (rate = 0.10)
        int earned = (int) Math.floor(subtotal.doubleValue() * earnRatePerReal);
        BigDecimal cashValue = BigDecimal.valueOf(earned).multiply(coinCashValue).setScale(2, RoundingMode.HALF_UP);

        // Teto de 20% do valor do subtotal para resgate
        BigDecimal maxDiscountAllowed = subtotal.multiply(maxDiscountPercentage).setScale(2, RoundingMode.HALF_UP);
        int maxPointsAllowedBySubtotal = maxDiscountAllowed.divide(coinCashValue, 0, RoundingMode.FLOOR).intValue();

        int maxRedeemable = Math.min(currentAccountBalance, maxPointsAllowedBySubtotal);
        BigDecimal maxRedeemableValue = BigDecimal.valueOf(maxRedeemable).multiply(coinCashValue).setScale(2, RoundingMode.HALF_UP);

        return new LoyaltyCalculationResult(earned, cashValue, maxRedeemable, maxRedeemableValue);
    }
}
