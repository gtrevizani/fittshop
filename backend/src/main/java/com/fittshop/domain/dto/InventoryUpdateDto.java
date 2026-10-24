package com.fittshop.domain.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public class InventoryUpdateDto {

    @NotNull(message = "ID do produto é obrigatório")
    private Long productId;

    @NotNull(message = "Quantidade delta é obrigatória")
    private Integer quantityDelta; // Positivo para reposição/fardo, negativo para ajuste

    private String operationType; // REPOSICAO, BAIXA_MANUAL, TRANSFERENCIA, AJUSTE_INVENTARIO
    private String batchCode;
    private LocalDate expirationDate;
    private String notes;

    public InventoryUpdateDto() {}

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public Integer getQuantityDelta() { return quantityDelta; }
    public void setQuantityDelta(Integer quantityDelta) { this.quantityDelta = quantityDelta; }

    public String getOperationType() { return operationType; }
    public void setOperationType(String operationType) { this.operationType = operationType; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
