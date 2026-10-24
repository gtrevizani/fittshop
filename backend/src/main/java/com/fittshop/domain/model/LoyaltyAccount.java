package com.fittshop.domain.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "loyalty_accounts")
public class LoyaltyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long customerId;

    @Column(nullable = false)
    private Integer fittCoinsBalance;

    @Column(nullable = false, length = 30)
    private String tierLevel; // BRONZE, PRATA, OURO, BLACK_BELT

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public LoyaltyAccount() {}

    public LoyaltyAccount(Long customerId, Integer fittCoinsBalance, String tierLevel) {
        this.customerId = customerId;
        this.fittCoinsBalance = fittCoinsBalance;
        this.tierLevel = tierLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }

    public Integer getFittCoinsBalance() { return fittCoinsBalance; }
    public void setFittCoinsBalance(Integer fittCoinsBalance) { this.fittCoinsBalance = fittCoinsBalance; }

    public String getTierLevel() { return tierLevel; }
    public void setTierLevel(String tierLevel) { this.tierLevel = tierLevel; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public void creditPoints(int points) {
        this.fittCoinsBalance = (this.fittCoinsBalance != null ? this.fittCoinsBalance : 0) + points;
        this.updatedAt = LocalDateTime.now();
        updateTier();
    }

    public void debitPoints(int points) {
        if (this.fittCoinsBalance == null || this.fittCoinsBalance < points) {
            throw new IllegalArgumentException("Saldo insuficiente de FittCoins.");
        }
        this.fittCoinsBalance -= points;
        this.updatedAt = LocalDateTime.now();
        updateTier();
    }

    private void updateTier() {
        if (this.fittCoinsBalance >= 1000) {
            this.tierLevel = "BLACK_BELT";
        } else if (this.fittCoinsBalance >= 500) {
            this.tierLevel = "OURO";
        } else if (this.fittCoinsBalance >= 200) {
            this.tierLevel = "PRATA";
        } else {
            this.tierLevel = "BRONZE";
        }
    }
}
