package com.fittshop.domain.model;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_audit_logs")
public class InventoryAuditLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long productId;

    @Column(nullable = false, length = 150)
    private String productName;

    @Column(nullable = false)
    private Integer quantityDelta;

    @Column(nullable = false)
    private Integer resultingStock;

    @Column(nullable = false, length = 50)
    private String operationType;

    @Column(length = 50)
    private String batchCode;

    private LocalDate expirationDate;

    @Column(length = 50)
    private String operatorRole;

    @Column(length = 255)
    private String notes;

    @Column(nullable = false, length = 64)
    private String hashSha256;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    public InventoryAuditLog() {}

    public InventoryAuditLog(Long productId, String productName, Integer quantityDelta, Integer resultingStock,
                             String operationType, String batchCode, LocalDate expirationDate,
                             String operatorRole, String notes, String hashSha256) {
        this.productId = productId;
        this.productName = productName;
        this.quantityDelta = quantityDelta;
        this.resultingStock = resultingStock;
        this.operationType = operationType;
        this.batchCode = batchCode;
        this.expirationDate = expirationDate;
        this.operatorRole = operatorRole;
        this.notes = notes;
        this.hashSha256 = hashSha256;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Long getProductId() { return productId; }
    public String getProductName() { return productName; }
    public Integer getQuantityDelta() { return quantityDelta; }
    public Integer getResultingStock() { return resultingStock; }
    public String getOperationType() { return operationType; }
    public String getBatchCode() { return batchCode; }
    public LocalDate getExpirationDate() { return expirationDate; }
    public String getOperatorRole() { return operatorRole; }
    public String getNotes() { return notes; }
    public String getHashSha256() { return hashSha256; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
