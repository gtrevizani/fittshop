package com.fittshop.service.notification;

import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {

    private static final Logger log = LoggerFactory.getLogger(NotificationService.class);

    private final Map<String, List<NotificationLog>> notificationHistory = new ConcurrentHashMap<>();

    public record NotificationLog(
            String id,
            String orderNumber,
            String channel,
            String recipient,
            String title,
            String message,
            LocalDateTime dispatchedAt,
            String status
    ) {}

    public void notifyOrderCreated(Order order, Customer customer) {
        String phone = customer != null && customer.getPhone() != null ? customer.getPhone() : "Atleta";
        String email = customer != null && customer.getEmail() != null ? customer.getEmail() : "atleta@fittshop.com.br";
        String name = customer != null && customer.getName() != null ? customer.getName() : "Atleta";

        String waMsg = String.format(java.util.Locale.US,
                "🏋️ Olá, %s! Seu pedido *%s* na FittShop foi gerado com sucesso! "
                + "Valor total: R$ %.2f. Sua reserva atômica de estoque está garantida por 15 minutos.",
                name, order.getOrderNumber(), order.getTotalAmount());

        dispatchWhatsApp(order.getOrderNumber(), phone, "Pedido Criado", waMsg);
        dispatchEmail(order.getOrderNumber(), email, "FittShop - Pedido " + order.getOrderNumber() + " Confirmado", waMsg);
    }

    public void notifyPaymentConfirmed(Order order, Customer customer) {
        String phone = customer != null && customer.getPhone() != null ? customer.getPhone() : "Atleta";
        String email = customer != null && customer.getEmail() != null ? customer.getEmail() : "atleta@fittshop.com.br";
        String name = customer != null && customer.getName() != null ? customer.getName() : "Atleta";

        String waMsg = String.format(
                "✅ Pagamento Aprovado, %s! Seu pedido *%s* já entrou em separação prioritária no nosso CD. "
                + "Você acumulou +%d FittCoins nesta compra!",
                name, order.getOrderNumber(), order.getFittCoinsEarned() != null ? order.getFittCoinsEarned() : 0);

        dispatchWhatsApp(order.getOrderNumber(), phone, "Pagamento Aprovado", waMsg);
        dispatchEmail(order.getOrderNumber(), email, "FittShop - Pagamento Confirmado!", waMsg);
    }

    public void notifyOrderStatusChanged(Order order, Customer customer, OrderStatus status) {
        String phone = customer != null && customer.getPhone() != null ? customer.getPhone() : "Atleta";
        String email = customer != null && customer.getEmail() != null ? customer.getEmail() : "atleta@fittshop.com.br";
        String name = customer != null && customer.getName() != null ? customer.getName() : "Atleta";

        String tracking = order.getTrackingCode() != null ? order.getTrackingCode() : "BR-ME" + order.getOrderNumber().replace("FITT-", "");
        String waMsg = String.format(
                "🚚 Atualização do Pedido *%s* para %s: Status agora é *%s*. Código de Rastreamento: *%s*.",
                order.getOrderNumber(), name, status, tracking);

        dispatchWhatsApp(order.getOrderNumber(), phone, "Atualização de Envio", waMsg);
        dispatchEmail(order.getOrderNumber(), email, "FittShop - Atualização do Pedido " + order.getOrderNumber(), waMsg);
    }

    public List<NotificationLog> getNotificationsForOrder(String orderNumber) {
        return notificationHistory.getOrDefault(orderNumber, Collections.emptyList());
    }

    private void dispatchWhatsApp(String orderNumber, String phone, String title, String message) {
        log.info("[NOTIFICAÇÃO WHATSAPP] Destinatário: {}, Título: {}, Mensagem: {}", phone, title, message);
        NotificationLog entry = new NotificationLog(
                UUID.randomUUID().toString(),
                orderNumber,
                "WHATSAPP",
                phone,
                title,
                message,
                LocalDateTime.now(),
                "DELIVERED"
        );
        notificationHistory.computeIfAbsent(orderNumber, k -> Collections.synchronizedList(new ArrayList<>())).add(entry);
    }

    private void dispatchEmail(String orderNumber, String email, String title, String message) {
        log.info("[NOTIFICAÇÃO E-MAIL] Destinatário: {}, Assunto: {}, Corpo: {}", email, title, message);
        NotificationLog entry = new NotificationLog(
                UUID.randomUUID().toString(),
                orderNumber,
                "EMAIL",
                email,
                title,
                message,
                LocalDateTime.now(),
                "DELIVERED"
        );
        notificationHistory.computeIfAbsent(orderNumber, k -> Collections.synchronizedList(new ArrayList<>())).add(entry);
    }
}
