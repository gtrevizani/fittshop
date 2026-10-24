package com.fittshop.domain.dto;

public class PaymentResult {
    private String transactionId;
    private String status; // PENDING, APPROVED, REJECTED
    private String pixCopiaECola;
    private String pixQrCodeBase64;
    private String message;

    public PaymentResult() {}

    public PaymentResult(String transactionId, String status, String pixCopiaECola, String pixQrCodeBase64, String message) {
        this.transactionId = transactionId;
        this.status = status;
        this.pixCopiaECola = pixCopiaECola;
        this.pixQrCodeBase64 = pixQrCodeBase64;
        this.message = message;
    }

    public String getTransactionId() { return transactionId; }
    public void setTransactionId(String transactionId) { this.transactionId = transactionId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPixCopiaECola() { return pixCopiaECola; }
    public void setPixCopiaECola(String pixCopiaECola) { this.pixCopiaECola = pixCopiaECola; }

    public String getPixQrCodeBase64() { return pixQrCodeBase64; }
    public void setPixQrCodeBase64(String pixQrCodeBase64) { this.pixQrCodeBase64 = pixQrCodeBase64; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
}
