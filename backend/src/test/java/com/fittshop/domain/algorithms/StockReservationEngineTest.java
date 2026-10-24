package com.fittshop.domain.algorithms;

import com.fittshop.domain.model.Product;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class StockReservationEngineTest {

    private final StockReservationEngine engine = new StockReservationEngine();

    @Test
    @DisplayName("Deve permitir reserva atômica quando houver saldo líquido disponível")
    void shouldApproveReservationWhenStockAvailable() {
        Product product = new Product("TEST-SKU", "Whey Test", "Desc", BigDecimal.TEN, "Suplementos",
                null, null, "HIPERTROFIA", 10, 2, "LOTE1", LocalDate.now().plusMonths(6), 1.0);
        product.setId(100L);

        // 10 físico - 2 reservado ativo = 8 disponíveis. Solicitando 3.
        StockReservationEngine.ReservationEvaluation eval = engine.evaluateAndCreate(product, 2, 3, 900);

        assertTrue(eval.isSuccess());
        assertNotNull(eval.getReservation());
        assertEquals(3, eval.getReservation().getQuantity());
        assertEquals("ACTIVE", eval.getReservation().getStatus());
        assertEquals(5, eval.getAvailableStock());
    }

    @Test
    @DisplayName("Deve barrar e lançar violação de integridade quando saldo líquido for insuficiente")
    void shouldDenyReservationWhenStockInsufficient() {
        Product product = new Product("TEST-SKU", "Whey Test", "Desc", BigDecimal.TEN, "Suplementos",
                null, null, "HIPERTROFIA", 5, 2, "LOTE1", LocalDate.now().plusMonths(6), 1.0);
        product.setId(100L);

        // 5 físico - 4 ativo = 1 disponível. Solicitando 2.
        StockReservationEngine.ReservationEvaluation eval = engine.evaluateAndCreate(product, 4, 2, 900);

        assertFalse(eval.isSuccess());
        assertNull(eval.getReservation());
        assertTrue(eval.getMessage().contains("Estoque insuficiente"));
    }
}
