package com.fittshop.service;

import com.fittshop.domain.algorithms.StockReservationEngine;
import com.fittshop.domain.dto.InventoryUpdateDto;
import com.fittshop.domain.model.Product;
import com.fittshop.domain.model.StockReservation;
import com.fittshop.repository.ProductRepository;
import com.fittshop.repository.StockReservationRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InventoryService {

    private final ProductRepository productRepository;
    private final StockReservationRepository stockReservationRepository;
    private final StockReservationEngine stockReservationEngine;
    private final com.fittshop.repository.InventoryAuditLogRepository inventoryAuditLogRepository;

    @Value("${fittshop.business.stock-lease-ttl-seconds:900}")
    private int stockLeaseTtlSeconds;

    public InventoryService(ProductRepository productRepository,
                            StockReservationRepository stockReservationRepository,
                            StockReservationEngine stockReservationEngine,
                            com.fittshop.repository.InventoryAuditLogRepository inventoryAuditLogRepository) {
        this.productRepository = productRepository;
        this.stockReservationRepository = stockReservationRepository;
        this.stockReservationEngine = stockReservationEngine;
        this.inventoryAuditLogRepository = inventoryAuditLogRepository;
    }

    public List<StockReservation> reserveStockForOrder(String reservationUuid, Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado ID: " + productId));

        int activeReserved = stockReservationRepository.getActiveReservedQuantity(productId, LocalDateTime.now());
        StockReservationEngine.ReservationEvaluation eval = stockReservationEngine.evaluateAndCreate(
                product, activeReserved, quantity, stockLeaseTtlSeconds
        );

        if (!eval.isSuccess()) {
            throw new IllegalStateException(eval.getMessage());
        }

        StockReservation res = eval.getReservation();
        res.setReservationUuid(reservationUuid);
        stockReservationRepository.save(res);

        List<StockReservation> result = new ArrayList<>();
        result.add(res);
        return result;
    }

    public void commitReservation(String reservationUuid) {
        List<StockReservation> reservations = stockReservationRepository.findByReservationUuid(reservationUuid);
        for (StockReservation res : reservations) {
            if ("ACTIVE".equals(res.getStatus())) {
                Product product = productRepository.findById(res.getProductId())
                        .orElseThrow(() -> new IllegalStateException("Produto não encontrado ID: " + res.getProductId()));

                int newQuantity = Math.max(0, product.getStockQuantity() - res.getQuantity());
                product.setStockQuantity(newQuantity);
                productRepository.save(product);

                res.setStatus("COMMITTED");
                stockReservationRepository.save(res);
            }
        }
    }

    public void releaseReservation(String reservationUuid) {
        List<StockReservation> reservations = stockReservationRepository.findByReservationUuid(reservationUuid);
        for (StockReservation res : reservations) {
            if ("ACTIVE".equals(res.getStatus())) {
                res.setStatus("CANCELLED");
                stockReservationRepository.save(res);
            }
        }
    }

    public Product processInventoryAdjustment(InventoryUpdateDto dto) {
        Product product = productRepository.findById(dto.getProductId())
                .orElseThrow(() -> new IllegalArgumentException("Produto não encontrado ID: " + dto.getProductId()));

        int current = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int newQuantity = current + dto.getQuantityDelta();
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Ajuste resultaria em estoque negativo. Operação cancelada.");
        }

        product.setStockQuantity(newQuantity);
        if (dto.getBatchCode() != null && !dto.getBatchCode().isBlank()) {
            product.setBatchCode(dto.getBatchCode());
        }
        if (dto.getExpirationDate() != null) {
            product.setExpirationDate(dto.getExpirationDate());
        }

        Product saved = productRepository.save(product);

        // Registro Imutável de Auditoria DevSecOps com Hash SHA-256
        String rawAudit = String.format(java.util.Locale.US, "%d|%s|%d|%d|%s|%s",
                product.getId(), product.getName(), dto.getQuantityDelta(), newQuantity,
                dto.getOperationType(), LocalDateTime.now());
        String sha256 = calculateSha256(rawAudit);

        com.fittshop.domain.model.InventoryAuditLog auditLog = new com.fittshop.domain.model.InventoryAuditLog(
                product.getId(),
                product.getName(),
                dto.getQuantityDelta(),
                newQuantity,
                dto.getOperationType() != null ? dto.getOperationType() : "REPOSICAO_LOTE",
                dto.getBatchCode(),
                dto.getExpirationDate(),
                "ROLE_STOCKIST",
                dto.getNotes(),
                sha256
        );
        inventoryAuditLogRepository.save(auditLog);

        return saved;
    }

    public List<com.fittshop.domain.model.InventoryAuditLog> getAuditLogs() {
        return inventoryAuditLogRepository.findTop50ByOrderByCreatedAtDesc();
    }

    private String calculateSha256(String data) {
        try {
            java.security.MessageDigest md = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
            StringBuilder hex = new StringBuilder();
            for (byte b : hash) {
                String h = Integer.toHexString(0xff & b);
                if (h.length() == 1) hex.append('0');
                hex.append(h);
            }
            return hex.toString();
        } catch (Exception e) {
            return java.util.UUID.randomUUID().toString().replace("-", "");
        }
    }

    @Scheduled(fixedRate = 60000) // Executa a cada 60s para liberar reservas expiradas
    public void cleanupExpiredReservations() {
        List<StockReservation> expired = stockReservationRepository.findExpiredReservations(LocalDateTime.now());
        for (StockReservation res : expired) {
            res.setStatus("EXPIRED");
            stockReservationRepository.save(res);
        }
    }
}
