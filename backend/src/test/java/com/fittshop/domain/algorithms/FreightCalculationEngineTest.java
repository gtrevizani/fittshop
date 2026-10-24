package com.fittshop.domain.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class FreightCalculationEngineTest {

    private final FreightCalculationEngine engine = new FreightCalculationEngine();

    @Test
    @DisplayName("Deve conceder frete grátis para Sudeste em compras a partir de R$ 199,00")
    void shouldGrantFreeShippingForSudeste() {
        FreightCalculationEngine.FreightResult result = engine.calculate(
                "01310-100", new BigDecimal("200.00"), 2.0, new BigDecimal("199.00"), new BigDecimal("299.00")
        );

        assertTrue(result.isFreeShipping());
        assertEquals(new BigDecimal("0.00"), result.getFreightAmount());
        assertEquals(2, result.getEstimatedDeliveryDays());
    }

    @Test
    @DisplayName("Deve cobrar taxa de frete padrão quando subtotal for inferior ao teto de isenção")
    void shouldChargeFreightWhenBelowThreshold() {
        FreightCalculationEngine.FreightResult result = engine.calculate(
                "01310-100", new BigDecimal("100.00"), 1.0, new BigDecimal("199.00"), new BigDecimal("299.00")
        );

        assertFalse(result.isFreeShipping());
        assertEquals(new BigDecimal("14.90"), result.getFreightAmount());
    }
}
