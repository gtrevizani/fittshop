package com.fittshop.service.fiscal;

import com.fittshop.domain.algorithms.SefazAccessKeyGenerator;
import com.fittshop.domain.dto.DanfeDto;
import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.Order;
import com.fittshop.repository.CustomerRepository;
import com.fittshop.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class FiscalInvoiceService {

    public static final String EMITTER_CNPJ = "48.234.901/0001-88";
    public static final String EMITTER_NAME = "FITTSHOP SUPLEMENTOS NUTRICIONAIS LTDA";
    public static final String EMITTER_IE = "149.882.310.114";
    public static final String EMITTER_ADDRESS = "Av. Paulista, 1000 - Bela Vista, Sao Paulo - SP, CEP 01310-100";
    public static final String CFOP_VENDA = "5.102";
    public static final String NATURE_OF_OPERATION = "VENDA DE MERCADORIA ADQUIRIDA OU RECEBIDA DE TERCEIROS";
    public static final String NCM_SUPLEMENTOS = "2106.90.90";

    private final OrderRepository orderRepository;
    private final CustomerRepository customerRepository;
    private final SefazAccessKeyGenerator keyGenerator;

    @Autowired
    public FiscalInvoiceService(OrderRepository orderRepository, CustomerRepository customerRepository) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.keyGenerator = new SefazAccessKeyGenerator();
    }

    public FiscalInvoiceService(OrderRepository orderRepository, CustomerRepository customerRepository, SefazAccessKeyGenerator keyGenerator) {
        this.orderRepository = orderRepository;
        this.customerRepository = customerRepository;
        this.keyGenerator = keyGenerator;
    }

    @Transactional(readOnly = true)
    public DanfeDto generateDanfe(String orderNumber) {
        Order order = orderRepository.findByOrderNumber(orderNumber)
                .orElseThrow(() -> new IllegalArgumentException("Pedido nao encontrado: " + orderNumber));

        if (!isFiscalEligible(order.getStatus())) {
            throw new IllegalStateException("Espelho DANFE disponivel apenas para pedidos pagos ou em expedicao. Status atual: " + order.getStatus());
        }

        Customer customer = customerRepository.findById(order.getCustomerId())
                .orElse(null);

        LocalDateTime emissionDate = order.getPaidAt() != null ? order.getPaidAt() : order.getCreatedAt();
        long invoiceNumber = order.getId() != null ? order.getId() : 1001L;

        String accessKey = keyGenerator.generateKey(EMITTER_CNPJ, invoiceNumber, emissionDate);
        String formattedKey = SefazAccessKeyGenerator.formatKey(accessKey);
        String protocol = "13526" + String.format("%010d", Math.abs(orderNumber.hashCode()) % 1_000_000_000L);

        DanfeDto danfe = new DanfeDto();
        danfe.setAccessKey(accessKey);
        danfe.setFormattedAccessKey(formattedKey);
        danfe.setProtocolNumber(protocol);
        danfe.setAuthorizationDate(emissionDate);
        danfe.setOrderNumber(order.getOrderNumber());
        danfe.setNatureOfOperation(NATURE_OF_OPERATION);
        danfe.setCfop(CFOP_VENDA);

        danfe.setEmitterCnpj(EMITTER_CNPJ);
        danfe.setEmitterName(EMITTER_NAME);
        danfe.setEmitterIe(EMITTER_IE);
        danfe.setEmitterAddress(EMITTER_ADDRESS);

        if (customer != null) {
            danfe.setRecipientName(customer.getName());
            danfe.setRecipientCpf(formatCpf(customer.getCpf()));
        } else {
            danfe.setRecipientName("Consumidor Final");
            danfe.setRecipientCpf("000.000.000-00");
        }

        danfe.setRecipientAddress(order.getDeliveryAddress() != null ? order.getDeliveryAddress() : "Retirada Balcao");
        danfe.setRecipientZipCode(order.getDeliveryZipCode() != null ? order.getDeliveryZipCode() : "00000-000");

        danfe.setSubtotal(order.getSubtotal());
        danfe.setDiscountAmount(order.getDiscountAmount());
        danfe.setFreightAmount(order.getFreightAmount());
        danfe.setTotalAmount(order.getTotalAmount());
        danfe.setComplementaryInfo("Documento fiscal auxiliar emitido por ME/EPP optante pelo Simples Nacional. Protocolo SEFAZ: " + protocol);

        List<DanfeDto.DanfeItemDto> items = order.getItems().stream()
                .map(item -> new DanfeDto.DanfeItemDto(
                        "FITT-" + item.getProductId(),
                        item.getProductName(),
                        NCM_SUPLEMENTOS,
                        CFOP_VENDA,
                        item.getQuantity(),
                        item.getUnitPrice(),
                        item.getTotalPrice()
                ))
                .collect(Collectors.toList());

        danfe.setItems(items);
        return danfe;
    }

    private boolean isFiscalEligible(OrderStatus status) {
        return status == OrderStatus.PAGO ||
               status == OrderStatus.EM_SEPARACAO ||
               status == OrderStatus.EXPEDIDO ||
               status == OrderStatus.ENTREGUE;
    }

    private String formatCpf(String raw) {
        if (raw == null) return "000.000.000-00";
        String clean = raw.replaceAll("\\D", "");
        if (clean.length() == 11) {
            return clean.substring(0, 3) + "." + clean.substring(3, 6) + "." + clean.substring(6, 9) + "-" + clean.substring(9, 11);
        }
        return raw;
    }
}
