package com.fittshop.controller;

import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.model.Order;
import com.fittshop.repository.OrderRepository;
import com.fittshop.service.CheckoutService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@Tag(name = "Pedidos & Rastreamento", description = "Endpoints para consulta de pedidos, rastreamento e atualização de status pelo Caixa/Estoquista")
@CrossOrigin(origins = "*")
public class OrderController {

    private final OrderRepository orderRepository;
    private final CheckoutService checkoutService;

    public OrderController(OrderRepository orderRepository, CheckoutService checkoutService) {
        this.orderRepository = orderRepository;
        this.checkoutService = checkoutService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os pedidos ou filtra por status e cliente")
    public ResponseEntity<List<Order>> getOrders(
            @RequestParam(required = false) OrderStatus status,
            @RequestParam(required = false) Long customerId) {
        if (customerId != null) {
            return ResponseEntity.ok(orderRepository.findByCustomerIdOrderByCreatedAtDesc(customerId));
        }
        if (status != null) {
            return ResponseEntity.ok(orderRepository.findByStatusOrderByCreatedAtDesc(status));
        }
        return ResponseEntity.ok(orderRepository.findAll());
    }

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Busca detalhes de um pedido pelo número de pedido")
    public ResponseEntity<Order> getByOrderNumber(@PathVariable String orderNumber) {
        return orderRepository.findByOrderNumber(orderNumber)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PatchMapping("/{orderNumber}/status")
    @Operation(summary = "Atualiza o status do pedido na FSM (EM_SEPARACAO, EXPEDIDO, ENTREGUE, CANCELADO)")
    public ResponseEntity<Order> updateStatus(
            @PathVariable String orderNumber,
            @RequestParam OrderStatus status) {
        return ResponseEntity.ok(checkoutService.updateOrderStatus(orderNumber, status));
    }
}
