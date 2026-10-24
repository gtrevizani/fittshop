package com.fittshop.service;

import com.fittshop.domain.algorithms.FreightCalculationEngine;
import com.fittshop.domain.algorithms.LoyaltyFittCoinsEngine;
import com.fittshop.domain.algorithms.SmartDiscountEngine;
import com.fittshop.domain.dto.CartItemDto;
import com.fittshop.domain.dto.CheckoutRequest;
import com.fittshop.domain.dto.CheckoutResponse;
import com.fittshop.domain.dto.FreightRequest;
import com.fittshop.domain.dto.FreightResponse;
import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.enums.PaymentMethod;
import com.fittshop.domain.enums.RoleType;
import com.fittshop.domain.model.*;
import com.fittshop.repository.*;
import com.fittshop.service.payment.PaymentGatewayService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class CheckoutService {

    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final LoyaltyAccountRepository loyaltyAccountRepository;
    private final InventoryService inventoryService;
    private final SmartDiscountEngine discountEngine;
    private final FreightCalculationEngine freightEngine;
    private final com.fittshop.service.freight.FreightCarrierService freightCarrierService;
    private final LoyaltyFittCoinsEngine loyaltyEngine;
    private final com.fittshop.service.payment.PaymentGatewayService paymentGatewayService;
    private final com.fittshop.service.affiliate.AffiliateService affiliateService;

    @Value("${fittshop.business.free-shipping-threshold-sudeste:199.00}")
    private BigDecimal thresholdSudeste;

    @Value("${fittshop.business.free-shipping-threshold-outros:299.00}")
    private BigDecimal thresholdOutros;

    @Value("${fittshop.business.fittcoins-points-rate:0.10}")
    private double fittCoinsPointsRate;

    @Value("${fittshop.business.fittcoins-redemption-value:0.50}")
    private BigDecimal fittCoinsRedemptionValue;

    @Value("${fittshop.business.fittcoins-max-discount-percentage:0.20}")
    private BigDecimal fittCoinsMaxDiscountPercentage;

    public CheckoutService(ProductRepository productRepository,
                           OrderRepository orderRepository,
                           CustomerRepository customerRepository,
                           LoyaltyAccountRepository loyaltyAccountRepository,
                           InventoryService inventoryService,
                           SmartDiscountEngine discountEngine,
                           FreightCalculationEngine freightEngine,
                           com.fittshop.service.freight.FreightCarrierService freightCarrierService,
                           LoyaltyFittCoinsEngine loyaltyEngine,
                           com.fittshop.service.payment.PaymentGatewayService paymentGatewayService,
                           com.fittshop.service.affiliate.AffiliateService affiliateService) {
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.loyaltyAccountRepository = loyaltyAccountRepository;
        this.inventoryService = inventoryService;
        this.discountEngine = discountEngine;
        this.freightEngine = freightEngine;
        this.freightCarrierService = freightCarrierService;
        this.loyaltyEngine = loyaltyEngine;
        this.paymentGatewayService = paymentGatewayService;
        this.affiliateService = affiliateService;
    }

    public FreightResponse calculateFreight(FreightRequest request) {
        double totalWeight = 0.5;
        BigDecimal subtotal = request.getSubtotal();

        if (request.getItems() != null && !request.getItems().isEmpty()) {
            BigDecimal computedSubtotal = BigDecimal.ZERO;
            for (CartItemDto item : request.getItems()) {
                Product p = productRepository.findById(item.getProductId()).orElse(null);
                if (p != null) {
                    computedSubtotal = computedSubtotal.add(p.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
                    totalWeight += (p.getWeightKg() * item.getQuantity());
                }
            }
            if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) == 0) {
                subtotal = computedSubtotal;
            }
        }

        FreightCalculationEngine.FreightResult result = freightCarrierService.calculateShipping(
                request.getZipCode(), subtotal, totalWeight, thresholdSudeste, thresholdOutros
        );

        return new FreightResponse(
                result.getFreightAmount(),
                result.getEstimatedDeliveryDays(),
                result.isFreeShipping(),
                result.getCarrierName()
        );
    }

    public CheckoutResponse processCheckout(CheckoutRequest request) {
        // 1. Obter ou Criar Cliente
        Customer customer = resolveCustomer(request);

        // 2. Resolver Itens e calcular subtotal
        String reservationUuid = UUID.randomUUID().toString();
        BigDecimal subtotal = BigDecimal.ZERO;
        List<String> productNames = new ArrayList<>();
        double totalWeight = 0.5;

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItemDto itemDto : request.getItems()) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new IllegalArgumentException("Produto inválido ID: " + itemDto.getProductId()));

            // Reserva atômica no estoque
            inventoryService.reserveStockForOrder(reservationUuid, product.getId(), itemDto.getQuantity());

            BigDecimal itemTotal = product.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            subtotal = subtotal.add(itemTotal);
            productNames.add(product.getName());
            totalWeight += (product.getWeightKg() * itemDto.getQuantity());

            OrderItem orderItem = new OrderItem(product.getId(), product.getName(), itemDto.getQuantity(), product.getPrice());
            orderItems.add(orderItem);
        }

        // 3. Resgate de FittCoins
        LoyaltyAccount loyaltyAccount = loyaltyAccountRepository.findByCustomerId(customer.getId())
                .orElseGet(() -> loyaltyAccountRepository.save(new LoyaltyAccount(customer.getId(), 0, "BRONZE")));

        int fittCoinsToRedeem = (request.getFittCoinsToRedeem() != null) ? request.getFittCoinsToRedeem() : 0;
        if (fittCoinsToRedeem > loyaltyAccount.getFittCoinsBalance()) {
            fittCoinsToRedeem = loyaltyAccount.getFittCoinsBalance();
        }

        // 4. Cálculo de Descontos (Combos + Cupons + FittCoins)
        SmartDiscountEngine.DiscountSummary discountSummary = discountEngine.calculateDiscounts(
                subtotal, productNames, request.getCouponCode(), fittCoinsToRedeem,
                fittCoinsRedemptionValue, fittCoinsMaxDiscountPercentage
        );

        // 5. Cálculo do Frete com Transportadoras / Melhor Envio
        FreightCalculationEngine.FreightResult freightResult = freightCarrierService.calculateShipping(
                request.getDeliveryZipCode(), discountSummary.getNetSubtotal(), totalWeight, thresholdSudeste, thresholdOutros
        );

        BigDecimal finalTotal = discountSummary.getNetSubtotal().add(freightResult.getFreightAmount()).setScale(2, RoundingMode.HALF_UP);

        // 6. Atualizar Saldo de FittCoins
        if (fittCoinsToRedeem > 0) {
            loyaltyAccount.debitPoints(fittCoinsToRedeem);
            loyaltyAccountRepository.save(loyaltyAccount);
        }

        LoyaltyFittCoinsEngine.LoyaltyCalculationResult loyaltyResult = loyaltyEngine.evaluate(
                finalTotal, loyaltyAccount.getFittCoinsBalance(), fittCoinsPointsRate,
                fittCoinsRedemptionValue, fittCoinsMaxDiscountPercentage
        );

        // 7. Salvar Pedido
        String orderNumber = "FITT-" + System.currentTimeMillis();
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setCustomerId(customer.getId());
        order.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);
        order.setSubtotal(subtotal);
        order.setDiscountAmount(discountSummary.getTotalDiscount());
        order.setFreightAmount(freightResult.getFreightAmount());
        order.setTotalAmount(finalTotal);
        order.setPaymentMethod(request.getPaymentMethod());
        order.setReservationUuid(reservationUuid);
        order.setSellerCode(request.getSellerCode());
        order.setDeliveryZipCode(request.getDeliveryZipCode());
        order.setDeliveryAddress(request.getDeliveryAddress());
        order.setFittCoinsEarned(loyaltyResult.getPointsEarned());
        order.setFittCoinsRedeemed(fittCoinsToRedeem);
        order.setCreatedAt(LocalDateTime.now());

        for (OrderItem oi : orderItems) {
            order.addItem(oi);
        }

        // 8. Processar Pagamento via Gateway Real (Mercado Pago / EMVCo PIX)
        com.fittshop.domain.dto.PaymentResult paymentResult = paymentGatewayService.processPayment(order, request.getPaymentMethod(), customer);
        order.setExternalTransactionId(paymentResult.getTransactionId());

        // 9. Registrar comissão de afiliado/personal trainer se aplicável
        String affiliateCandidate = (request.getSellerCode() != null && !request.getSellerCode().isBlank())
                ? request.getSellerCode() : request.getCouponCode();
        if (affiliateCandidate != null && !affiliateCandidate.isBlank()) {
            affiliateService.registerOrderCommission(order, affiliateCandidate);
        }

        orderRepository.save(order);

        // 9. Construir Resposta com payload de pagamento oficial
        CheckoutResponse response = new CheckoutResponse();
        response.setOrderNumber(orderNumber);
        response.setStatus(order.getStatus());
        response.setSubtotal(subtotal);
        response.setDiscountAmount(discountSummary.getTotalDiscount());
        response.setFreightAmount(freightResult.getFreightAmount());
        response.setTotalAmount(finalTotal);
        response.setPaymentMethod(request.getPaymentMethod());
        response.setReservationUuid(reservationUuid);
        response.setReservationExpiresAt(LocalDateTime.now().plusSeconds(900));
        response.setFittCoinsEarned(loyaltyResult.getPointsEarned());
        response.setExternalTransactionId(paymentResult.getTransactionId());
        response.setPixCopiaECola(paymentResult.getPixCopiaECola());
        response.setPixQrCodeBase64(paymentResult.getPixQrCodeBase64());
        response.setMessage(paymentResult.getMessage());

        return response;
    }

    public Order confirmPayment(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + orderNumber));

        if (order.getStatus() == OrderStatus.AGUARDANDO_PAGAMENTO || order.getStatus() == OrderStatus.CRIADO) {
            order.setStatus(OrderStatus.PAGO);
            order.setPaidAt(LocalDateTime.now());
            order.setTrackingCode("FIT" + (100000 + new Random().nextInt(900000)) + "BR");

            // Consolida baixa de estoque definitiva
            if (order.getReservationUuid() != null) {
                inventoryService.commitReservation(order.getReservationUuid());
            }

            // Credita FittCoins na conta do cliente
            if (order.getFittCoinsEarned() != null && order.getFittCoinsEarned() > 0) {
                LoyaltyAccount acc = loyaltyAccountRepository.findByCustomerId(order.getCustomerId())
                        .orElseGet(() -> new LoyaltyAccount(order.getCustomerId(), 0, "BRONZE"));
                acc.creditPoints(order.getFittCoinsEarned());
                loyaltyAccountRepository.save(acc);
            }

            // Aprova comissão do consultor/afiliado
            affiliateService.approveCommissionForOrder(order.getOrderNumber());

            return orderRepository.save(order);
        }
        return order;
    }

    public Order confirmPaymentByExternalTransaction(String externalTxId) {
        Order order = orderRepository.findAll().stream()
                .filter(o -> externalTxId.equals(o.getExternalTransactionId()))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado para transação externa: " + externalTxId));
        return confirmPayment(order.getOrderNumber());
    }

    public PaymentGatewayService getPaymentGatewayService() {
        return paymentGatewayService;
    }

    public Order updateOrderStatus(String orderNumber, OrderStatus newStatus) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Pedido não encontrado: " + orderNumber));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    private Customer resolveCustomer(CheckoutRequest request) {
        if (request.getCustomerId() != null) {
            return customerRepository.findById(request.getCustomerId()).orElse(null);
        }

        String email = (request.getCustomerEmail() != null && !request.getCustomerEmail().isBlank())
                ? request.getCustomerEmail() : "atleta@fittshop.com.br";

        return customerRepository.findByEmail(email).orElseGet(() -> {
            Customer c = new Customer();
            c.setName(request.getCustomerName() != null ? request.getCustomerName() : "Atleta FittShop");
            c.setEmail(email);
            c.setPassword("{noop}fitt123");
            c.setCpf(request.getCustomerCpf() != null ? request.getCustomerCpf() : "123.456.789-00");
            c.setPhone(request.getCustomerPhone() != null ? request.getCustomerPhone() : "(11) 99999-9999");
            c.setRole(RoleType.ROLE_CUSTOMER);
            return customerRepository.save(c);
        });
    }
}
