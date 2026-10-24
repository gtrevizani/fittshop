package com.fittshop.audit;

import com.fittshop.domain.algorithms.FreightCalculationEngine;
import com.fittshop.domain.algorithms.LoyaltyFittCoinsEngine;
import com.fittshop.domain.algorithms.SmartDiscountEngine;
import com.fittshop.domain.algorithms.StockReservationEngine;
import com.fittshop.domain.model.Product;
import com.fittshop.service.affiliate.AffiliateService;
import com.fittshop.service.payment.MercadoPagoPaymentGatewayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Suite Canônica de Verificação dos Invariantes de Domínio FittShop (AGENT DEVSECOPS & AGENT JUDGE).
 */
public class DevSecOpsInvariantAuditTest {

    private final StockReservationEngine stockEngine = new StockReservationEngine();
    private final SmartDiscountEngine discountEngine = new SmartDiscountEngine();
    private final LoyaltyFittCoinsEngine loyaltyEngine = new LoyaltyFittCoinsEngine();
    private final FreightCalculationEngine freightEngine = new FreightCalculationEngine();
    private final MercadoPagoPaymentGatewayService paymentGateway = new MercadoPagoPaymentGatewayService();

    @Test
    @DisplayName("[INVARIANTE RN03] Trava Absoluta de Overselling no Estoque")
    void testInvariantOversellingLock() {
        Product product = new Product();
        product.setId(1L);
        product.setName("Creatina 300g");
        product.setStockQuantity(5);

        // Físico: 5, Reservado ativo: 2 -> Saldo disponível real: 3
        var evalRejeitada = stockEngine.evaluateAndCreate(product, 2, 4, 900);
        var evalAprovada = stockEngine.evaluateAndCreate(product, 2, 3, 900);

        assertFalse(evalRejeitada.isSuccess(), "É proibido reservar quantidade maior do que o saldo disponível");
        assertEquals(3, evalRejeitada.getAvailableStock());
        assertTrue(evalAprovada.isSuccess(), "Deve permitir reservar exatamente o saldo disponível");
        assertNotNull(evalAprovada.getReservation());
    }

    @Test
    @DisplayName("[INVARIANTE RN04] Teto de Resgate de FittCoins Limitado a 20% do Subtotal")
    void testInvariantFittCoinsMaxRedemptionCap() {
        BigDecimal subtotal = new BigDecimal("200.00");
        int balance = 200; // 200 * 0.50 = R$ 100,00 tentado
        double pointsRate = 0.10;
        BigDecimal pointValue = new BigDecimal("0.50");
        BigDecimal maxDiscountRate = new BigDecimal("0.20"); // Teto 20% = R$ 40,00

        LoyaltyFittCoinsEngine.LoyaltyCalculationResult result = loyaltyEngine.evaluate(
                subtotal, balance, pointsRate, pointValue, maxDiscountRate
        );

        assertNotNull(result);
        assertEquals(80, result.getMaxRedeemablePoints(), "Não deve permitir resgatar mais de 80 pontos (R$ 40,00)");
        assertEquals(new BigDecimal("40.00"), result.getMaxRedeemableCashValue(), "Desconto máximo deve respeitar teto de 20%");
    }

    @Test
    @DisplayName("[INVARIANTE RN05] Frete Grátis Sudeste a partir de R$ 199,00")
    void testInvariantFreeShippingSudeste() {
        BigDecimal thresholdSudeste = new BigDecimal("199.00");
        BigDecimal thresholdOutros = new BigDecimal("299.00");

        FreightCalculationEngine.FreightResult freeResult = freightEngine.calculate(
                "01310-100", new BigDecimal("199.00"), 2.0, thresholdSudeste, thresholdOutros
        );
        FreightCalculationEngine.FreightResult chargedResult = freightEngine.calculate(
                "01310-100", new BigDecimal("198.99"), 2.0, thresholdSudeste, thresholdOutros
        );

        assertTrue(freeResult.isFreeShipping(), "Subtotal >= 199 deve conceder frete grátis no Sudeste");
        assertEquals(new BigDecimal("0.00"), freeResult.getFreightAmount());
        assertFalse(chargedResult.isFreeShipping(), "Subtotal < 199 deve cobrar taxa de frete");
    }

    @Test
    @DisplayName("[INVARIANTE RN06] Desconto de Combos Whey + Creatina (15%)")
    void testInvariantComboDiscount() {
        BigDecimal subtotal = new BigDecimal("300.00");
        List<String> items = List.of("100% Whey Gold Standard 900g", "Creatina Creapure 300g Micronizada");

        SmartDiscountEngine.DiscountSummary summary = discountEngine.calculateDiscounts(
                subtotal, items, null, 0, new BigDecimal("0.50"), new BigDecimal("0.20")
        );

        assertEquals(new BigDecimal("45.00"), summary.getComboDiscount(), "Combo Whey + Creatina deve aplicar 15% de desconto");
        assertEquals(new BigDecimal("255.00"), summary.getNetSubtotal());
    }

    @Test
    @DisplayName("[INVARIANTE RN08] Repasse de Comissão de Afiliados Credenciados")
    void testInvariantAffiliateCommissionCalculation() {
        AffiliateService service = new AffiliateService(null);
        var validation = service.validateCode("SARAH10");

        assertTrue(validation.isValid());
        assertEquals(new BigDecimal("0.10"), validation.getCommissionRate());

        BigDecimal orderSubtotal = new BigDecimal("250.00");
        BigDecimal expectedCommission = orderSubtotal.multiply(validation.getCommissionRate()).setScale(2, java.math.RoundingMode.HALF_UP);
        assertEquals(new BigDecimal("25.00"), expectedCommission, "Comissão de 10% de R$ 250,00 deve ser R$ 25,00");
    }

    @Test
    @DisplayName("[INVARIANTE SEGURANÇA] Validação de Checksum EMVCo Bacen no PIX")
    void testInvariantPixCrc16Compliance() {
        String payload = paymentGateway.generateEmvCoPixPayload("FITT-SEC-01", new BigDecimal("150.00"), "pix@fittshop.com.br");

        assertNotNull(payload);
        assertTrue(payload.startsWith("00020126"));
        String crc = payload.substring(payload.length() - 4);
        assertEquals(4, crc.length());
        assertTrue(crc.matches("[0-9A-Fa-f]{4}"), "CRC16 do Bacen deve ser hexadecimal estrito");
    }
}
