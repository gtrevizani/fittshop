package com.fittshop.service.payment;

import com.fittshop.domain.dto.PaymentResult;
import com.fittshop.domain.enums.PaymentMethod;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.Order;

public interface PaymentGatewayService {
    PaymentResult processPayment(Order order, PaymentMethod method, Customer customer);
    boolean handleWebhook(String payload, String signatureHeader);
}
