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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/checkout")
@Tag(name = "Checkout & Pagamentos", description = "Endpoints para cálculo de frete, finalização de compra e confirmação de pagamento")
@CrossOrigin(origins = "*")
public class CheckoutController {

    private final CheckoutService checkoutService;

    public CheckoutController(CheckoutService checkoutService) {
        this.checkoutService = checkoutService;
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
    @Operation(summary = "Webhook / Confirmação de Pagamento (PIX, Cartão ou Baixa pelo Caixa)")
    public ResponseEntity<Order> confirmPayment(@PathVariable String orderNumber) {
        return ResponseEntity.ok(checkoutService.confirmPayment(orderNumber));
    }
}
