package com.fittshop.domain.algorithms;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SmartDiscountEngineTest {

    private final SmartDiscountEngine engine = new SmartDiscountEngine();

    @Test
    @DisplayName("Deve aplicar desconto de combo de 15% quando carrinho contiver Whey e Creatina")
    void shouldApplyComboDiscount() {
        BigDecimal subtotal = new BigDecimal("200.00");
        List<String> products = List.of("100% Whey Protein", "Creatina Creapure");

        SmartDiscountEngine.DiscountSummary summary = engine.calculateDiscounts(
                subtotal, products, null, 0, new BigDecimal("0.50"), new BigDecimal("0.20")
        );

        assertEquals(new BigDecimal("30.00"), summary.getComboDiscount()); // 15% de 200 = 30
        assertEquals(new BigDecimal("170.00"), summary.getNetSubtotal());
    }

    @Test
    @DisplayName("Deve respeitar o teto de 20% no resgate de FittCoins")
    void shouldRespectFittCoinsDiscountCap() {
        BigDecimal subtotal = new BigDecimal("100.00");
        // Tentando resgatar 80 FittCoins (80 * 0.50 = R$ 40,00). Mas teto é 20% = R$ 20,00.
        SmartDiscountEngine.DiscountSummary summary = engine.calculateDiscounts(
                subtotal, List.of(), null, 80, new BigDecimal("0.50"), new BigDecimal("0.20")
        );

        assertEquals(new BigDecimal("20.00"), summary.getFittCoinsDiscount());
        assertEquals(new BigDecimal("80.00"), summary.getNetSubtotal());
    }
}
