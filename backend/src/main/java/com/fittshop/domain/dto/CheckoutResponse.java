package com.fittshop.domain.dto;

import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CheckoutResponse {

    private String orderNumber;
    private OrderStatus status;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal freightAmount;
    private BigDecimal totalAmount;
    private PaymentMethod paymentMethod;

    private String reservationUuid;
    private LocalDateTime reservationExpiresAt;

    private String pixQrCodeBase64;
    private String pixCopiaECola;
    private Integer fittCoinsEarned;
    private String externalTransactionId;
    private String message;

    public CheckoutResponse() {}

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public OrderStatus getStatus() { return status; }
    public void setStatus(OrderStatus status) { this.status = status; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getReservationUuid() { return reservationUuid; }
    public void setReservationUuid(String reservationUuid) { this.reservationUuid = reservationUuid; }

    public LocalDateTime getReservationExpiresAt() { return reservationExpiresAt; }
    public void setReservationExpiresAt(LocalDateTime reservationExpiresAt) { this.reservationExpiresAt = reservationExpiresAt; }

    public String getPixQrCodeBase64() { return pixQrCodeBase64; }
    public void setPixQrCodeBase64(String pixQrCodeBase64) { this.pixQrCodeBase64 = pixQrCodeBase64; }

    public String getPixCopiaECola() { return pixCopiaECola; }
    public void setPixCopiaECola(String pixCopiaECola) { this.pixCopiaECola = pixCopiaECola; }

    public Integer getFittCoinsEarned() { return fittCoinsEarned; }
    public void setFittCoinsEarned(Integer fittCoinsEarned) { this.fittCoinsEarned = fittCoinsEarned; }

    public String getExternalTransactionId() { return externalTransactionId; }
    public void setExternalTransactionId(String externalTransactionId) { this.externalTransactionId = externalTransactionId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
