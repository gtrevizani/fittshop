package com.fittshop.service.payment;

import com.fittshop.domain.dto.PaymentResult;
import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.enums.PaymentMethod;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class MercadoPagoPaymentGatewayTest {

    private final MercadoPagoPaymentGatewayService gateway = new MercadoPagoPaymentGatewayService();

    @Test
    @DisplayName("Deve gerar payload PIX EMVCo válido com CRC16 de 4 caracteres hexadecimais")
    void shouldGenerateValidEmvCoPixPayload() {
        String payload = gateway.generateEmvCoPixPayload("FITT-1001", new BigDecimal("189.90"), "fittshop@fittshop.com.br");

        assertNotNull(payload);
        assertTrue(payload.startsWith("00020126"), "Payload deve iniciar com identificador EMVCo");
        assertTrue(payload.contains("189.90"), "Payload deve conter o valor da compra");
        assertTrue(payload.contains("FITTSHOP SUPLEMENTOS"), "Payload deve conter o nome do recebedor");
        assertTrue(payload.contains("6304"), "Payload deve conter o campo 6304 do checksum");

        String crc = payload.substring(payload.length() - 4);
        assertEquals(4, crc.length(), "CRC16 deve ter exatamente 4 caracteres");
        assertTrue(crc.matches("[0-9A-Fa-f]{4}"), "CRC16 deve ser hexadecimal válido");
    }

    @Test
    @DisplayName("Deve processar pagamento PIX gerando status PENDING e QR Code Base64")
    void shouldProcessPixPayment() {
        Order order = new Order();
        order.setOrderNumber("FITT-9999");
        order.setTotalAmount(new BigDecimal("250.00"));
        order.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
        order.setCreatedAt(LocalDateTime.now());

        Customer customer = new Customer();
        customer.setName("Carlos Atleta");
        customer.setEmail("carlos@atleta.com.br");

        PaymentResult result = gateway.processPayment(order, PaymentMethod.PIX, customer);

        assertNotNull(result);
        assertEquals("PENDING", result.getStatus());
        assertNotNull(result.getTransactionId());
        assertTrue(result.getTransactionId().startsWith("MP-"));
        assertNotNull(result.getPixCopiaECola());
        assertNotNull(result.getPixQrCodeBase64());
        assertTrue(result.getPixQrCodeBase64().startsWith("data:image/svg+xml;base64,"));
    }

    @Test
    @DisplayName("Deve aceitar webhook de pagamento válido do Mercado Pago")
    void shouldHandleWebhook() {
        String payload = "{\"action\":\"payment.updated\",\"data\":{\"id\":\"123456789\"}}";
        boolean handled = gateway.handleWebhook(payload, "ts=123,v1=abc");
        assertTrue(handled);

        assertFalse(gateway.handleWebhook(null, null));
        assertFalse(gateway.handleWebhook("", ""));
    }
}
