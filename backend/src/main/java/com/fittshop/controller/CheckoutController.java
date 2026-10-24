package com.fittshop.controller;

import com.fittshop.domain.dto.CheckoutRequest;
import com.fittshop.domain.dto.CheckoutResponse;
import com.fittshop.domain.dto.FreightRequest;
import com.fittshop.domain.dto.FreightResponse;
import com.fittshop.domain.model.Order;
import com.fittshop.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@Tag(name = "Checkout & Pagamentos", description = "Endpoints para cálculo de frete, finalização de compra e confirmação de pagamento")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private static final Logger log = LoggerFactory.getLogger(CheckoutController.class);

    private final CheckoutService checkoutService;
    private final com.fittshop.service.coupon.CouponService couponService;

    public CheckoutController(CheckoutService checkoutService,
                              com.fittshop.service.coupon.CouponService couponService) {
        this.checkoutService = checkoutService;
        this.couponService = couponService;
    }

    @PostMapping("/validate-coupon")
    @Operation(summary = "Valida cupom promocional no banco de dados com anti-abuso por CPF")
    public ResponseEntity<com.fittshop.service.coupon.CouponService.CouponValidationResult> validateCoupon(
            @RequestBody java.util.Map<String, Object> body) {
        String code = body.get("code") != null ? body.get("code").toString() : null;
        String cpf = body.get("cpf") != null ? body.get("cpf").toString() : null;
        Object subtotalObj = body.get("subtotal");
        java.math.BigDecimal subtotal = subtotalObj != null ? new java.math.BigDecimal(subtotalObj.toString()) : java.math.BigDecimal.ZERO;
        return ResponseEntity.ok(couponService.validateCoupon(code, cpf, subtotal));
    }

    @PostMapping("/freight")
    @Operation(summary = "Calcula valor e prazo do frete por CEP")
    public ResponseEntity<FreightResponse> calculateFreight(@RequestBody FreightRequest request) {
        return ResponseEntity.ok(checkoutService.calculateFreight(request));
    }

    @PostMapping
    @Operation(summary = "Processa o checkout, aloca reserva atômica de estoque e gera dados de pagamento")
    public ResponseEntity<CheckoutResponse> processCheckout(@Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.processCheckout(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/confirm/{orderNumber}")
    @Operation(summary = "Webhook / Confirmação de Pagamento manual ou síncrona")
    public ResponseEntity<Order> confirmPayment(@PathVariable String orderNumber) {
        return ResponseEntity.ok(checkoutService.confirmPayment(orderNumber));
    }

    @PostMapping("/webhook/payment")
    @Operation(summary = "Webhook assíncrono oficial para Gateways de Pagamento (Mercado Pago / Asaas)")
    public ResponseEntity<java.util.Map<String, Object>> handlePaymentWebhook(
            @RequestBody(required = false) java.util.Map<String, Object> payload,
            @RequestHeader(value = "x-signature", required = false) String signature,
            @RequestParam(value = "orderNumber", required = false) String orderNumber,
            @RequestParam(value = "data.id", required = false) String externalTxId) {

        String payloadStr = payload != null ? payload.toString() : "";
        checkoutService.getPaymentGatewayService().handleWebhook(payloadStr, signature);

        if (orderNumber != null && !orderNumber.isBlank()) {
            checkoutService.confirmPayment(orderNumber);
        } else if (externalTxId != null && !externalTxId.isBlank()) {
            try {
                checkoutService.confirmPaymentByExternalTransaction(externalTxId);
            } catch (Exception ex) {
                log.error("Falha ao confirmar pagamento via webhook para externalTxId {}: {}", externalTxId, ex.getMessage(), ex);
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(java.util.Map.of("status", "ERROR", "message", "Erro interno ao processar transacao: " + ex.getMessage()));
            }
        } else if (payload != null && payload.containsKey("orderNumber")) {
            checkoutService.confirmPayment(payload.get("orderNumber").toString());
        }

        return ResponseEntity.ok(java.util.Map.of("status", "RECEIVED", "message", "Webhook processado com sucesso"));
    }
}
