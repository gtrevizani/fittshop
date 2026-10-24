package com.fittshop.service.affiliate;

import com.fittshop.domain.dto.AffiliateCommissionSummary;
import com.fittshop.domain.dto.AffiliateValidateResponse;
import com.fittshop.domain.model.AffiliateCommission;
import com.fittshop.domain.model.Order;
import com.fittshop.repository.AffiliateCommissionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class AffiliateService {

    private static final Logger log = LoggerFactory.getLogger(AffiliateService.class);

    private final AffiliateCommissionRepository commissionRepository;

    // Tabela canônica de afiliados e personal trainers credenciados FittShop
    private static final Map<String, AffiliateInfo> AFFILIATE_REGISTRY = Map.of(
            "SARAH10", new AffiliateInfo("Sarah Nutricionista Esportiva", new BigDecimal("0.10"), new BigDecimal("0.10")),
            "TREINADORVIP", new AffiliateInfo("Prof. Rodrigo Personal Trainer", new BigDecimal("0.12"), new BigDecimal("0.10")),
            "FITCOACH", new AffiliateInfo("Coach Felipe Performance", new BigDecimal("0.15"), new BigDecimal("0.05")),
            "ALEX10", new AffiliateInfo("Alex Crossfit Head Coach", new BigDecimal("0.10"), new BigDecimal("0.10")),
            "FITT10", new AffiliateInfo("Cupom Oficial FittShop 10%", BigDecimal.ZERO, new BigDecimal("0.10")),
            "MONSTRO15", new AffiliateInfo("Cupom Promocional Monstro 15%", BigDecimal.ZERO, new BigDecimal("0.15")),
            "BEMVINDO20", new AffiliateInfo("Cupom de Boas-Vindas 20%", BigDecimal.ZERO, new BigDecimal("0.20"))
    );

    private record AffiliateInfo(String name, BigDecimal commissionRate, BigDecimal clientDiscountRate) {}

    public AffiliateService(AffiliateCommissionRepository commissionRepository) {
        this.commissionRepository = commissionRepository;
    }

    public AffiliateValidateResponse validateCode(String rawCode) {
        if (rawCode == null || rawCode.isBlank()) {
            return new AffiliateValidateResponse(false, "", "", BigDecimal.ZERO, BigDecimal.ZERO);
        }
        String cleanCode = rawCode.trim().toUpperCase();
        AffiliateInfo info = AFFILIATE_REGISTRY.get(cleanCode);

        if (info != null) {
            return new AffiliateValidateResponse(true, cleanCode, info.name(), info.commissionRate(), info.clientDiscountRate());
        }

        // Se for qualquer outro código com padrão de personal (inicia com PERSONAL ou COACH)
        if (cleanCode.startsWith("PERSONAL") || cleanCode.startsWith("COACH")) {
            return new AffiliateValidateResponse(true, cleanCode, "Consultor Esportivo Credenciado",
                    new BigDecimal("0.10"), new BigDecimal("0.05"));
        }

        return new AffiliateValidateResponse(false, cleanCode, "Código não encontrado", BigDecimal.ZERO, BigDecimal.ZERO);
    }

    public AffiliateCommission registerOrderCommission(Order order, String rawCode) {
        AffiliateValidateResponse validation = validateCode(rawCode);
        if (!validation.isValid()) {
            return null;
        }

        BigDecimal rate = validation.getCommissionRate();
        BigDecimal commissionAmount = order.getSubtotal().multiply(rate).setScale(2, RoundingMode.HALF_UP);

        AffiliateCommission commission = new AffiliateCommission(
                validation.getAffiliateCode(),
                order.getId(),
                order.getOrderNumber(),
                order.getTotalAmount(),
                rate,
                commissionAmount,
                "PENDING"
        );

        order.setAffiliateCode(validation.getAffiliateCode());
        order.setCommissionAmount(commissionAmount);

        log.info("Comissão de afiliado registrada: Cupom: {}, Pedido: {}, Valor: R${}",
                validation.getAffiliateCode(), order.getOrderNumber(), commissionAmount);

        return commissionRepository.save(commission);
    }

    public void approveCommissionForOrder(String orderNumber) {
        commissionRepository.findByOrderNumber(orderNumber).ifPresent(comm -> {
            comm.setStatus("APPROVED");
            log.info("Comissão de afiliado aprovada com sucesso para pedido: {}, Valor: R${}",
                    orderNumber, comm.getCommissionAmount());
            commissionRepository.save(comm);
        });
    }

    public AffiliateCommissionSummary getStatement(String code) {
        String cleanCode = (code != null) ? code.trim().toUpperCase() : "";
        List<AffiliateCommission> list = commissionRepository.findByAffiliateCode(cleanCode);

        BigDecimal totalEarned = BigDecimal.ZERO;
        BigDecimal pending = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;

        for (AffiliateCommission c : list) {
            totalEarned = totalEarned.add(c.getCommissionAmount());
            if ("PENDING".equals(c.getStatus())) {
                pending = pending.add(c.getCommissionAmount());
            } else if ("PAID".equals(c.getStatus()) || "APPROVED".equals(c.getStatus())) {
                paid = paid.add(c.getCommissionAmount());
            }
        }

        return new AffiliateCommissionSummary(cleanCode, totalEarned, pending, paid, list.size(), list);
    }
}
