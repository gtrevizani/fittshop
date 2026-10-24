package com.fittshop.service.notification;

import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.Order;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificationServiceTest {

    private final NotificationService notificationService = new NotificationService();

    @Test
    @DisplayName("Deve disparar notificações de pedido criado via WhatsApp e E-mail")
    void shouldNotifyOrderCreated() {
        Order order = new Order();
        order.setOrderNumber("FITT-8801");
        order.setTotalAmount(new BigDecimal("199.90"));

        Customer customer = new Customer();
        customer.setName("Guilherme Atleta");
        customer.setPhone("(11) 98888-0001");
        customer.setEmail("guilherme@fittshop.com.br");

        notificationService.notifyOrderCreated(order, customer);

        List<NotificationService.NotificationLog> logs = notificationService.getNotificationsForOrder("FITT-8801");
        assertNotNull(logs);
        assertEquals(2, logs.size(), "Deveria registrar 2 canais (WhatsApp e E-mail)");

        assertTrue(logs.stream().anyMatch(l -> "WHATSAPP".equals(l.channel())));
        assertTrue(logs.stream().anyMatch(l -> "EMAIL".equals(l.channel())));
        assertTrue(logs.stream().anyMatch(l -> l.message().contains("FITT-8801")));
    }

    @Test
    @DisplayName("Deve disparar notificações de pagamento aprovado com saldo de FittCoins")
    void shouldNotifyPaymentConfirmed() {
        Order order = new Order();
        order.setOrderNumber("FITT-8802");
        order.setTotalAmount(new BigDecimal("350.00"));
        order.setFittCoinsEarned(35);

        Customer customer = new Customer();
        customer.setName("Sarah Fit");
        customer.setPhone("(21) 97777-0002");
        customer.setEmail("sarah@fittshop.com.br");

        notificationService.notifyPaymentConfirmed(order, customer);

        List<NotificationService.NotificationLog> logs = notificationService.getNotificationsForOrder("FITT-8802");
        assertEquals(2, logs.size());
        assertTrue(logs.get(0).message().contains("+35 FittCoins") || logs.get(1).message().contains("+35 FittCoins"));
    }

    @Test
    @DisplayName("Deve disparar notificações de alteração de status com código de rastreio")
    void shouldNotifyOrderStatusChanged() {
        Order order = new Order();
        order.setOrderNumber("FITT-8803");
        order.setTrackingCode("BR-ME99887766");

        Customer customer = new Customer();
        customer.setName("Marcos Powerlifter");

        notificationService.notifyOrderStatusChanged(order, customer, OrderStatus.EXPEDIDO);

        List<NotificationService.NotificationLog> logs = notificationService.getNotificationsForOrder("FITT-8803");
        assertEquals(2, logs.size());
        assertTrue(logs.stream().anyMatch(l -> l.message().contains("BR-ME99887766")));
    }
}
