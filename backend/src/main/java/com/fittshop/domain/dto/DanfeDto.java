package com.fittshop.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class DanfeDto {

    private String accessKey;
    private String formattedAccessKey;
    private String protocolNumber;
    private LocalDateTime authorizationDate;
    private String orderNumber;
    private String natureOfOperation;
    private String cfop;
    private String emitterCnpj;
    private String emitterName;
    private String emitterIe;
    private String emitterAddress;
    private String recipientName;
    private String recipientCpf;
    private String recipientAddress;
    private String recipientZipCode;
    private BigDecimal subtotal;
    private BigDecimal discountAmount;
    private BigDecimal freightAmount;
    private BigDecimal totalAmount;
    private String complementaryInfo;
    private List<DanfeItemDto> items;

    public DanfeDto() {}

    public static class DanfeItemDto {
        private String productCode;
        private String description;
        private String ncm;
        private String cfop;
        private Integer quantity;
        private BigDecimal unitPrice;
        private BigDecimal totalPrice;

        public DanfeItemDto() {}

        public DanfeItemDto(String productCode, String description, String ncm, String cfop, Integer quantity, BigDecimal unitPrice, BigDecimal totalPrice) {
            this.productCode = productCode;
            this.description = description;
            this.ncm = ncm;
            this.cfop = cfop;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.totalPrice = totalPrice;
        }

        public String getProductCode() { return productCode; }
        public void setProductCode(String productCode) { this.productCode = productCode; }

        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }

        public String getNcm() { return ncm; }
        public void setNcm(String ncm) { this.ncm = ncm; }

        public String getCfop() { return cfop; }
        public void setCfop(String cfop) { this.cfop = cfop; }

        public Integer getQuantity() { return quantity; }
        public void setQuantity(Integer quantity) { this.quantity = quantity; }

        public BigDecimal getUnitPrice() { return unitPrice; }
        public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

        public BigDecimal getTotalPrice() { return totalPrice; }
        public void setTotalPrice(BigDecimal totalPrice) { this.totalPrice = totalPrice; }
    }

    public String getAccessKey() { return accessKey; }
    public void setAccessKey(String accessKey) { this.accessKey = accessKey; }

    public String getFormattedAccessKey() { return formattedAccessKey; }
    public void setFormattedAccessKey(String formattedAccessKey) { this.formattedAccessKey = formattedAccessKey; }

    public String getProtocolNumber() { return protocolNumber; }
    public void setProtocolNumber(String protocolNumber) { this.protocolNumber = protocolNumber; }

    public LocalDateTime getAuthorizationDate() { return authorizationDate; }
    public void setAuthorizationDate(LocalDateTime authorizationDate) { this.authorizationDate = authorizationDate; }

    public String getOrderNumber() { return orderNumber; }
    public void setOrderNumber(String orderNumber) { this.orderNumber = orderNumber; }

    public String getNatureOfOperation() { return natureOfOperation; }
    public void setNatureOfOperation(String natureOfOperation) { this.natureOfOperation = natureOfOperation; }

    public String getCfop() { return cfop; }
    public void setCfop(String cfop) { this.cfop = cfop; }

    public String getEmitterCnpj() { return emitterCnpj; }
    public void setEmitterCnpj(String emitterCnpj) { this.emitterCnpj = emitterCnpj; }

    public String getEmitterName() { return emitterName; }
    public void setEmitterName(String emitterName) { this.emitterName = emitterName; }

    public String getEmitterIe() { return emitterIe; }
    public void setEmitterIe(String emitterIe) { this.emitterIe = emitterIe; }

    public String getEmitterAddress() { return emitterAddress; }
    public void setEmitterAddress(String emitterAddress) { this.emitterAddress = emitterAddress; }

    public String getRecipientName() { return recipientName; }
    public void setRecipientName(String recipientName) { this.recipientName = recipientName; }

    public String getRecipientCpf() { return recipientCpf; }
    public void setRecipientCpf(String recipientCpf) { this.recipientCpf = recipientCpf; }

    public String getRecipientAddress() { return recipientAddress; }
    public void setRecipientAddress(String recipientAddress) { this.recipientAddress = recipientAddress; }

    public String getRecipientZipCode() { return recipientZipCode; }
    public void setRecipientZipCode(String recipientZipCode) { this.recipientZipCode = recipientZipCode; }

    public BigDecimal getSubtotal() { return subtotal; }
    public void setSubtotal(BigDecimal subtotal) { this.subtotal = subtotal; }

    public BigDecimal getDiscountAmount() { return discountAmount; }
    public void setDiscountAmount(BigDecimal discountAmount) { this.discountAmount = discountAmount; }

    public BigDecimal getFreightAmount() { return freightAmount; }
    public void setFreightAmount(BigDecimal freightAmount) { this.freightAmount = freightAmount; }

    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }

    public String getComplementaryInfo() { return complementaryInfo; }
    public void setComplementaryInfo(String complementaryInfo) { this.complementaryInfo = complementaryInfo; }

    public List<DanfeItemDto> getItems() { return items; }
    public void setItems(List<DanfeItemDto> items) { this.items = items; }
}
