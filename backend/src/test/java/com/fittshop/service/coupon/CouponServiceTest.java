package com.fittshop.service.coupon;

import com.fittshop.domain.model.Coupon;
import com.fittshop.repository.CouponRepository;
import com.fittshop.repository.CouponUsageRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@DisplayName("Servico de Cupons Promocionais e Prevencao de Abuso por CPF")
class CouponServiceTest {

    private CouponRepository couponRepository;
    private CouponUsageRepository couponUsageRepository;
    private CouponService couponService;

    @BeforeEach
    void setUp() {
        couponRepository = Mockito.mock(CouponRepository.class);
        couponUsageRepository = Mockito.mock(CouponUsageRepository.class);
        couponService = new CouponService(couponRepository, couponUsageRepository);
    }

    @Test
    @DisplayName("Deve calcular desconto percentual respeitando teto maximo")
    void shouldApplyPercentageDiscountWithCap() {
        Coupon coupon = new Coupon(
                "PRIMEIRACOMPRA", "15% OFF", Coupon.DiscountType.PERCENTAGE,
                new BigDecimal("15.00"), new BigDecimal("100.00"), new BigDecimal("50.00"),
                1, LocalDateTime.now().plusDays(30), true
        );
        coupon.setId(10L);

        when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("PRIMEIRACOMPRA")).thenReturn(Optional.of(coupon));
        when(couponUsageRepository.countByCouponIdAndCustomerCpf(eq(10L), any())).thenReturn(0L);

        // Subtotal de 200: 15% = 30.00 (abaixo do teto de 50)
        CouponService.CouponValidationResult res1 = couponService.validateCoupon("PRIMEIRACOMPRA", "52998224725", new BigDecimal("200.00"));
        assertTrue(res1.isValid());
        assertEquals(new BigDecimal("30.00"), res1.getDiscountAmount());

        // Subtotal de 400: 15% = 60.00 -> Limitado a 50.00
        CouponService.CouponValidationResult res2 = couponService.validateCoupon("PRIMEIRACOMPRA", "52998224725", new BigDecimal("400.00"));
        assertTrue(res2.isValid());
        assertEquals(new BigDecimal("50.00"), res2.getDiscountAmount());
    }

    @Test
    @DisplayName("Deve rejeitar cupom quando limite de uso por CPF for ultrapassado")
    void shouldRejectWhenUsageLimitPerCpfExceeded() {
        Coupon coupon = new Coupon(
                "PRIMEIRACOMPRA", "15% OFF", Coupon.DiscountType.PERCENTAGE,
                new BigDecimal("15.00"), new BigDecimal("100.00"), new BigDecimal("50.00"),
                1, LocalDateTime.now().plusDays(30), true
        );
        coupon.setId(10L);

        when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("PRIMEIRACOMPRA")).thenReturn(Optional.of(coupon));
        // Ja usado 1 vez
        when(couponUsageRepository.countByCouponIdAndCustomerCpf(10L, "52998224725")).thenReturn(1L);

        CouponService.CouponValidationResult res = couponService.validateCoupon("PRIMEIRACOMPRA", "52998224725", new BigDecimal("200.00"));
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("Limite de 1 uso(s) deste cupom já foi atingido"));
    }

    @Test
    @DisplayName("Deve rejeitar quando subtotal for inferior ao valor minimo")
    void shouldRejectWhenBelowMinOrderAmount() {
        Coupon coupon = new Coupon(
                "VIPATLETA25", "R$ 25 OFF", Coupon.DiscountType.FIXED_AMOUNT,
                new BigDecimal("25.00"), new BigDecimal("150.00"), new BigDecimal("25.00"),
                1, LocalDateTime.now().plusDays(30), true
        );
        coupon.setId(11L);

        when(couponRepository.findByCodeIgnoreCaseAndActiveTrue("VIPATLETA25")).thenReturn(Optional.of(coupon));

        CouponService.CouponValidationResult res = couponService.validateCoupon("VIPATLETA25", "52998224725", new BigDecimal("100.00"));
        assertFalse(res.isValid());
        assertTrue(res.getMessage().contains("Valor mínimo"));
        assertTrue(res.getMessage().contains("150"));
    }
}
