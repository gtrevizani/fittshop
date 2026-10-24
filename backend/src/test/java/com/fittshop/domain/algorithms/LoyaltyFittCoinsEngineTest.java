package com.fittshop.domain.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class LoyaltyFittCoinsEngineTest {

    private final LoyaltyFittCoinsEngine engine = new LoyaltyFittCoinsEngine();

    @Test
    @DisplayName("Deve creditar 1 FittCoin a cada R$ 10 gastos")
    void shouldAccrueOneCoinPerTenReais() {
        LoyaltyFittCoinsEngine.LoyaltyCalculationResult result = engine.evaluate(
                new BigDecimal("250.00"), 50, 0.10, new BigDecimal("0.50"), new BigDecimal("0.20")
        );

        assertEquals(25, result.getPointsEarned());
        assertEquals(new BigDecimal("12.50"), result.getCashValueEarned());
        // Max discount 20% of 250 = R$ 50 -> 100 points. Account has 50 -> maxRedeemable = 50
        assertEquals(50, result.getMaxRedeemablePoints());
        assertEquals(new BigDecimal("25.00"), result.getMaxRedeemableCashValue());
    }
}
