package com.fittshop.service.fiscal;

import com.fittshop.domain.dto.DanfeDto;
import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.enums.PaymentMethod;
import com.fittshop.domain.enums.RoleType;
import com.fittshop.domain.model.Customer;
import com.fittshop.domain.model.Order;
import com.fittshop.domain.model.OrderItem;
import com.fittshop.repository.CustomerRepository;
import com.fittshop.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("Servico Fiscal de Emissao de DANFE e Chave de Acesso SEFAZ")
class FiscalInvoiceServiceTest {

    private OrderRepository orderRepository;
    private CustomerRepository customerRepository;
    private FiscalInvoiceService fiscalInvoiceService;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        customerRepository = Mockito.mock(CustomerRepository.class);
        fiscalInvoiceService = new FiscalInvoiceService(orderRepository, customerRepository);
    }

    @Test
    @DisplayName("Deve gerar DANFE oficial com chave de 44 digitos para pedido PAGO")
    void shouldGenerateDanfeForPaidOrder() {
        String orderNumber = "FITT-20261024-001";
        Order order = new Order();
        order.setId(1050L);
        order.setOrderNumber(orderNumber);
        order.setCustomerId(1L);
        order.setStatus(OrderStatus.PAGO);
        order.setSubtotal(new BigDecimal("189.90"));
        order.setDiscountAmount(BigDecimal.ZERO);
        order.setFreightAmount(new BigDecimal("15.00"));
        order.setTotalAmount(new BigDecimal("204.90"));
        order.setPaymentMethod(PaymentMethod.PIX);
        order.setDeliveryAddress("Av. Paulista, 1578, Bela Vista, Sao Paulo/SP");
        order.setDeliveryZipCode("01310-200");
        order.setCreatedAt(LocalDateTime.now().minusHours(1));
        order.setPaidAt(LocalDateTime.now());

        OrderItem item = new OrderItem(1L, "Whey Protein Isolado 900g", 1, new BigDecimal("189.90"));
        order.getItems().add(item);

        Customer customer = new Customer("Carlos Atleta", "carlos@atleta.com", "pass", "12345678909", "11988887777", RoleType.ROLE_CUSTOMER);
        customer.setId(1L);

        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));

        DanfeDto danfe = fiscalInvoiceService.generateDanfe(orderNumber);

        assertNotNull(danfe);
        assertEquals(orderNumber, danfe.getOrderNumber());
        assertEquals("48.234.901/0001-88", danfe.getEmitterCnpj());
        assertEquals("FITTSHOP SUPLEMENTOS NUTRICIONAIS LTDA", danfe.getEmitterName());
        assertEquals("Carlos Atleta", danfe.getRecipientName());
        assertEquals("123.456.789-09", danfe.getRecipientCpf());
        assertEquals(44, danfe.getAccessKey().length());
        assertNotNull(danfe.getFormattedAccessKey());
        assertNotNull(danfe.getProtocolNumber());
        assertEquals(1, danfe.getItems().size());
        assertEquals("2106.90.90", danfe.getItems().get(0).getNcm());
        assertEquals("5.102", danfe.getItems().get(0).getCfop());
    }

    @Test
    @DisplayName("Deve rejeitar emissao de DANFE para pedido AGUARDANDO_PAGAMENTO")
    void shouldRejectDanfeForPendingPaymentOrder() {
        String orderNumber = "FITT-20261024-PENDING";
        Order order = new Order();
        order.setOrderNumber(orderNumber);
        order.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);

        when(orderRepository.findByOrderNumber(orderNumber)).thenReturn(Optional.of(order));

        IllegalStateException ex = assertThrows(IllegalStateException.class,
                () -> fiscalInvoiceService.generateDanfe(orderNumber));
        assertTrue(ex.getMessage().contains("Espelho DANFE disponivel apenas para pedidos pagos"));
    }
}
