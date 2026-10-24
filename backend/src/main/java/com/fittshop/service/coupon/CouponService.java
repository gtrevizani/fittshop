package com.fittshop.service.coupon;

import com.fittshop.domain.model.Coupon;
import com.fittshop.domain.model.CouponUsage;
import com.fittshop.domain.validation.CpfValidator;
import com.fittshop.repository.CouponRepository;
import com.fittshop.repository.CouponUsageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;

@Service
public class CouponService {

    private static final Logger log = LoggerFactory.getLogger(CouponService.class);

    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;

    public CouponService(CouponRepository couponRepository, CouponUsageRepository couponUsageRepository) {
        this.couponRepository = couponRepository;
        this.couponUsageRepository = couponUsageRepository;
    }

    public static class CouponValidationResult {
        private final boolean valid;
        private final String code;
        private final String description;
        private final BigDecimal discountAmount;
        private final String message;

        public CouponValidationResult(boolean valid, String code, String description, BigDecimal discountAmount, String message) {
            this.valid = valid;
            this.code = code;
            this.description = description;
            this.discountAmount = discountAmount;
            this.message = message;
        }

        public boolean isValid() { return valid; }
        public String getCode() { return code; }
        public String getDescription() { return description; }
        public BigDecimal getDiscountAmount() { return discountAmount; }
        public String getMessage() { return message; }
    }

    @Transactional(readOnly = true)
    public CouponValidationResult validateCoupon(String code, String rawCpf, BigDecimal subtotal) {
        if (code == null || code.trim().isEmpty()) {
            return new CouponValidationResult(false, null, null, BigDecimal.ZERO, "Código do cupom não informado.");
        }
        if (subtotal == null || subtotal.compareTo(BigDecimal.ZERO) <= 0) {
            return new CouponValidationResult(false, code, null, BigDecimal.ZERO, "Subtotal do pedido inválido para cálculo de cupom.");
        }

        String cleanCode = code.trim().toUpperCase();
        Coupon coupon = couponRepository.findByCodeIgnoreCaseAndActiveTrue(cleanCode)
                .orElse(null);

        if (coupon == null) {
            return new CouponValidationResult(false, cleanCode, null, BigDecimal.ZERO, "Cupom não encontrado ou inativo: " + cleanCode);
        }

        if (coupon.getExpirationDate() != null && LocalDateTime.now().isAfter(coupon.getExpirationDate())) {
            return new CouponValidationResult(false, cleanCode, coupon.getDescription(), BigDecimal.ZERO, "Este cupom expirou em " + coupon.getExpirationDate());
        }

        if (coupon.getMinOrderAmount() != null && subtotal.compareTo(coupon.getMinOrderAmount()) < 0) {
            return new CouponValidationResult(false, cleanCode, coupon.getDescription(), BigDecimal.ZERO,
                    String.format("Valor mínimo para este cupom é de R$ %.2f (Subtotal atual: R$ %.2f)",
                            coupon.getMinOrderAmount(), subtotal));
        }

        String cleanCpf = rawCpf != null ? rawCpf.replaceAll("\\D", "") : "";
        if (!cleanCpf.isEmpty()) {
            if (!CpfValidator.isValid(cleanCpf)) {
                return new CouponValidationResult(false, cleanCode, coupon.getDescription(), BigDecimal.ZERO, "CPF do atleta inválido para validação do cupom.");
            }

            long usages = couponUsageRepository.countByCouponIdAndCustomerCpf(coupon.getId(), cleanCpf);
            if (coupon.getUsageLimitPerCpf() != null && usages >= coupon.getUsageLimitPerCpf()) {
                return new CouponValidationResult(false, cleanCode, coupon.getDescription(), BigDecimal.ZERO,
                        String.format("Limite de %d uso(s) deste cupom já foi atingido para o CPF informado.",
                                coupon.getUsageLimitPerCpf()));
            }
        }

        BigDecimal discount;
        if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
            BigDecimal percent = coupon.getDiscountValue().divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
            discount = subtotal.multiply(percent).setScale(2, RoundingMode.HALF_UP);
        } else {
            discount = coupon.getDiscountValue().setScale(2, RoundingMode.HALF_UP);
        }

        if (coupon.getMaxDiscountAmount() != null && discount.compareTo(coupon.getMaxDiscountAmount()) > 0) {
            discount = coupon.getMaxDiscountAmount();
        }

        if (discount.compareTo(subtotal) > 0) {
            discount = subtotal;
        }

        return new CouponValidationResult(true, coupon.getCode(), coupon.getDescription(), discount, "Cupom aplicado com sucesso!");
    }

    @Transactional
    public void recordCouponUsage(String code, String rawCpf, String orderNumber) {
        if (code == null || code.trim().isEmpty() || rawCpf == null || rawCpf.trim().isEmpty()) {
            return;
        }
        String cleanCode = code.trim().toUpperCase();
        String cleanCpf = rawCpf.replaceAll("\\D", "");

        couponRepository.findByCodeIgnoreCase(cleanCode).ifPresent(coupon -> {
            CouponUsage usage = new CouponUsage(coupon.getId(), cleanCpf, orderNumber);
            couponUsageRepository.save(usage);
            log.info("Uso de cupom gravado no banco: Cupom={}, CPF={}, Pedido={}", cleanCode, cleanCpf, orderNumber);
        });
    }
}
