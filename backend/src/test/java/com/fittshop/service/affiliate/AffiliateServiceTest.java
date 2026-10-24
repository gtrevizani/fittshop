package com.fittshop.service.affiliate;

import com.fittshop.domain.dto.AffiliateCommissionSummary;
import com.fittshop.domain.dto.AffiliateValidateResponse;
import com.fittshop.domain.enums.OrderStatus;
import com.fittshop.domain.model.AffiliateCommission;
import com.fittshop.domain.model.Order;
import com.fittshop.repository.AffiliateCommissionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class AffiliateServiceTest {

    private AffiliateCommissionRepository repository;
    private AffiliateService affiliateService;

    @BeforeEach
    void setUp() {
        repository = mock(AffiliateCommissionRepository.class);
        affiliateService = new AffiliateService(repository);
    }

    @Test
    @DisplayName("Deve validar cupom de personal trainer SARAH10 com comissão de 10%")
    void shouldValidatePersonalCouponSarah10() {
        AffiliateValidateResponse res = affiliateService.validateCode("SARAH10");

        assertTrue(res.isValid());
        assertEquals("SARAH10", res.getAffiliateCode());
        assertEquals(new BigDecimal("0.10"), res.getCommissionRate());
        assertEquals("Sarah Nutricionista Esportiva", res.getAffiliateName());
    }

    @Test
    @DisplayName("Deve validar código genérico de personal com prefixo PERSONAL")
    void shouldValidateGenericPersonalCode() {
        AffiliateValidateResponse res = affiliateService.validateCode("PERSONAL_GABRIEL");

        assertTrue(res.isValid());
        assertEquals("PERSONAL_GABRIEL", res.getAffiliateCode());
        assertEquals(new BigDecimal("0.10"), res.getCommissionRate());
    }

    @Test
    @DisplayName("Deve rejeitar código inválido")
    void shouldRejectInvalidCode() {
        AffiliateValidateResponse res = affiliateService.validateCode("CUPOM_INEXISTENTE");
        assertFalse(res.isValid());
    }

    @Test
    @DisplayName("Deve registrar comissão de 10% no pedido e retornar AffiliateCommission PENDING")
    void shouldRegisterOrderCommission() {
        Order order = new Order();
        order.setId(101L);
        order.setOrderNumber("FITT-TEST-01");
        order.setSubtotal(new BigDecimal("200.00"));
        order.setTotalAmount(new BigDecimal("200.00"));
        order.setStatus(OrderStatus.AGUARDANDO_PAGAMENTO);

        when(repository.save(any(AffiliateCommission.class))).thenAnswer(invocation -> invocation.getArgument(0));

        AffiliateCommission comm = affiliateService.registerOrderCommission(order, "SARAH10");

        assertNotNull(comm);
        assertEquals("SARAH10", comm.getAffiliateCode());
        assertEquals(new BigDecimal("20.00"), comm.getCommissionAmount());
        assertEquals("PENDING", comm.getStatus());
        assertEquals("SARAH10", order.getAffiliateCode());
        assertEquals(new BigDecimal("20.00"), order.getCommissionAmount());
    }

    @Test
    @DisplayName("Deve aprovar comissão de afiliado quando pedido for confirmado")
    void shouldApproveCommissionWhenPaid() {
        AffiliateCommission comm = new AffiliateCommission(
                "SARAH10", 101L, "FITT-TEST-01", new BigDecimal("200.00"),
                new BigDecimal("0.10"), new BigDecimal("20.00"), "PENDING"
        );
        when(repository.findByOrderNumber("FITT-TEST-01")).thenReturn(Optional.of(comm));

        affiliateService.approveCommissionForOrder("FITT-TEST-01");

        assertEquals("APPROVED", comm.getStatus());
        verify(repository, times(1)).save(comm);
    }

    @Test
    @DisplayName("Deve gerar extrato consolidado de comissões do afiliado")
    void shouldGenerateCommissionStatement() {
        List<AffiliateCommission> list = new ArrayList<>();
        list.add(new AffiliateCommission("SARAH10", 1L, "FITT-1", new BigDecimal("100.00"), new BigDecimal("0.10"), new BigDecimal("10.00"), "APPROVED"));
        list.add(new AffiliateCommission("SARAH10", 2L, "FITT-2", new BigDecimal("200.00"), new BigDecimal("0.10"), new BigDecimal("20.00"), "PENDING"));

        when(repository.findByAffiliateCode("SARAH10")).thenReturn(list);

        AffiliateCommissionSummary summary = affiliateService.getStatement("SARAH10");

        assertNotNull(summary);
        assertEquals(2, summary.getTotalOrders());
        assertEquals(new BigDecimal("30.00"), summary.getTotalEarned());
        assertEquals(new BigDecimal("20.00"), summary.getPendingAmount());
        assertEquals(new BigDecimal("10.00"), summary.getPaidAmount());
    }
}
