package com.fittshop.service.freight;

import com.fittshop.domain.algorithms.FreightCalculationEngine;
import com.fittshop.domain.algorithms.FreightCalculationEngine.FreightResult;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class MelhorEnvioFreightCarrierServiceTest {

    private final FreightCalculationEngine fallbackEngine = new FreightCalculationEngine();
    private final MelhorEnvioFreightCarrierService carrierService = new MelhorEnvioFreightCarrierService(fallbackEngine);

    @Test
    @DisplayName("Deve acionar fallback regional quando token Melhor Envio não estiver preenchido")
    void shouldTriggerFallbackWhenTokenIsEmpty() {
        FreightResult result = carrierService.calculateShipping(
                "01310-100",
                new BigDecimal("100.00"),
                1.5,
                new BigDecimal("199.00"),
                new BigDecimal("299.00")
        );

        assertNotNull(result);
        assertFalse(result.isFreeShipping());
        assertEquals(new BigDecimal("15.65"), result.getFreightAmount());
        assertEquals("FittLog Transportes", result.getCarrierName());
    }

    @Test
    @DisplayName("Deve conceder frete grátis na cotação com valor acima do threshold para Sudeste")
    void shouldGrantFreeShippingAboveThreshold() {
        FreightResult result = carrierService.calculateShipping(
                "04538-133",
                new BigDecimal("210.00"),
                2.0,
                new BigDecimal("199.00"),
                new BigDecimal("299.00")
        );

        assertNotNull(result);
        assertTrue(result.isFreeShipping());
        assertEquals(new BigDecimal("0.00"), result.getFreightAmount());
    }

    @Test
    @DisplayName("Deve calcular cubagem e tarifas diferenciadas para Região Sul")
    void shouldCalculateRatesForSouthRegion() {
        FreightResult result = carrierService.calculateVolumetricRate(
                "80010-000",
                new BigDecimal("80.00"),
                2.0,
                new BigDecimal("199.00"),
                new BigDecimal("299.00")
        );

        assertNotNull(result);
        assertFalse(result.isFreeShipping());
        assertEquals(new BigDecimal("16.80"), result.getFreightAmount());
        assertTrue(result.getCarrierName().contains("Melhor Envio"));
    }
}
