package com.fittshop.domain.dto;

import com.fittshop.domain.enums.PaymentMethod;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public class CheckoutRequest {

    private Long customerId;

    private String customerName;
    private String customerEmail;
    private String customerCpf;
    private String customerPhone;

    @NotEmpty(message = "O carrinho não pode estar vazio")
    private List<CartItemDto> items;

    @NotNull(message = "Forma de pagamento é obrigatória")
    private PaymentMethod paymentMethod;

    private String couponCode;
    private Integer fittCoinsToRedeem;
    private String sellerCode; // Código de vendedora opcional

    private String deliveryZipCode;
    private String deliveryAddress;

    public CheckoutRequest() {}

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public String getCustomerName() { return customerName; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }

    public String getCustomerEmail() { return customerEmail; }
    public void setCustomerEmail(String customerEmail) { this.customerEmail = customerEmail; }

    public String getCustomerCpf() { return customerCpf; }
    public void setCustomerCpf(String customerCpf) { this.customerCpf = customerCpf; }

    public String getCustomerPhone() { return customerPhone; }
    public void setCustomerPhone(String customerPhone) { this.customerPhone = customerPhone; }

    public List<CartItemDto> getItems() { return items; }
    public void setItems(List<CartItemDto> items) { this.items = items; }

    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getCouponCode() { return couponCode; }
    public void setCouponCode(String couponCode) { this.couponCode = couponCode; }

    public Integer getFittCoinsToRedeem() { return fittCoinsToRedeem; }
    public void setFittCoinsToRedeem(Integer fittCoinsToRedeem) { this.fittCoinsToRedeem = fittCoinsToRedeem; }

    public String getSellerCode() { return sellerCode; }
    public void setSellerCode(String sellerCode) { this.sellerCode = sellerCode; }

    public String getDeliveryZipCode() { return deliveryZipCode; }
    public void setDeliveryZipCode(String deliveryZipCode) { this.deliveryZipCode = deliveryZipCode; }

    public String getDeliveryAddress() { return deliveryAddress; }
    public void setDeliveryAddress(String deliveryAddress) { this.deliveryAddress = deliveryAddress; }
}
