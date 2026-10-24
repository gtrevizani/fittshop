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

    @Test
    @DisplayName("Deve validar assinatura HMAC-SHA256 do Webhook oficial")
    void shouldVerifyHmacSha256Signature() {
        String payload = "{\"data\":{\"id\":\"987654\"}}";
        String secret = "test_webhook_secret_key_123";
        String ts = "1710000000";

        // Gera assinatura HMAC esperada: ts:[ts];body:[payload]
        String manifest = "ts:" + ts + ";body:" + payload;
        try {
            javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
            mac.init(new javax.crypto.spec.SecretKeySpec(secret.getBytes(java.nio.charset.StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] hash = mac.doFinal(manifest.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            String v1 = hexString.toString();
            String signatureHeader = "ts=" + ts + ",v1=" + v1;

            assertTrue(gateway.verifySignature(payload, signatureHeader, secret));
            assertFalse(gateway.verifySignature(payload, signatureHeader, "wrong_secret"));
            assertFalse(gateway.verifySignature("tampered_payload", signatureHeader, secret));
        } catch (Exception e) {
            fail("Erro ao testar HMAC: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("Deve invocar PaymentClient do SDK Oficial Mercado Pago quando accessToken configurado")
    void shouldProcessPaymentViaOfficialMercadoPagoSdk() throws Exception {
        com.mercadopago.client.payment.PaymentClient mockClient = org.mockito.Mockito.mock(com.mercadopago.client.payment.PaymentClient.class);
        com.mercadopago.resources.payment.Payment mockPayment = org.mockito.Mockito.mock(com.mercadopago.resources.payment.Payment.class);
        com.mercadopago.resources.payment.PaymentPointOfInteraction mockPoi = org.mockito.Mockito.mock(com.mercadopago.resources.payment.PaymentPointOfInteraction.class);
        com.mercadopago.resources.payment.PaymentTransactionData mockTxData = org.mockito.Mockito.mock(com.mercadopago.resources.payment.PaymentTransactionData.class);

        org.mockito.Mockito.when(mockPayment.getId()).thenReturn(987654321L);
        org.mockito.Mockito.when(mockPayment.getStatus()).thenReturn("pending");
        org.mockito.Mockito.when(mockPayment.getPointOfInteraction()).thenReturn(mockPoi);
        org.mockito.Mockito.when(mockPoi.getTransactionData()).thenReturn(mockTxData);
        org.mockito.Mockito.when(mockTxData.getQrCode()).thenReturn("00020126...official_mp_qr");
        org.mockito.Mockito.when(mockTxData.getQrCodeBase64()).thenReturn("iVBORw0KGgoAAAANSUhEUgAA...");

        org.mockito.Mockito.when(mockClient.create(org.mockito.ArgumentMatchers.any())).thenReturn(mockPayment);

        MercadoPagoPaymentGatewayService sdkGateway = new MercadoPagoPaymentGatewayService(mockClient, "TEST-APP-TOKEN-123456");

        Order order = new Order();
        order.setOrderNumber("FITT-8899");
        order.setTotalAmount(new BigDecimal("199.90"));

        Customer customer = new Customer();
        customer.setName("Guilherme");
        customer.setEmail("guilherme@fittshop.com.br");
        customer.setCpf("52998224725");

        PaymentResult result = sdkGateway.processPayment(order, PaymentMethod.PIX, customer);

        assertNotNull(result);
        assertEquals("MP-987654321", result.getTransactionId());
        assertEquals("PENDING", result.getStatus());
        assertEquals("00020126...official_mp_qr", result.getPixCopiaECola());
        assertTrue(result.getPixQrCodeBase64().startsWith("data:image/png;base64,"));
        assertTrue(result.getMessage().contains("SDK Oficial"));
    }
}
