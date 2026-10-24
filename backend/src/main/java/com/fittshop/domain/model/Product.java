package com.fittshop.domain.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String sku;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 50)
    private String category; // Suplementos, Roupas, Acessórios, Combos

    @Column(length = 255)
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String nutritionalInfo; // Tabela nutricional / Composição técnica

    @Column(length = 50)
    private String targetGoal; // HIPERTROFIA, DEFINICAO, FORCA_POWERLIFTING, RESISTENCIA_CARDIO

    @Column(nullable = false)
    private Integer stockQuantity;

    @Column(nullable = false)
    private Integer minStockThreshold;

    @Column(length = 50)
    private String batchCode; // Código de Lote de Fornecedor

    private LocalDate expirationDate; // Validade (ANVISA)

    private Double weightKg; // Peso em kg para cálculo de frete

    @Version
    private Long version; // Bloqueio otimista contra race conditions

    public Product() {}

    public Product(String sku, String name, String description, BigDecimal price, String category,
                   String imageUrl, String nutritionalInfo, String targetGoal, Integer stockQuantity,
                   Integer minStockThreshold, String batchCode, LocalDate expirationDate, Double weightKg) {
        this.sku = sku;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.imageUrl = imageUrl;
        this.nutritionalInfo = nutritionalInfo;
        this.targetGoal = targetGoal;
        this.stockQuantity = stockQuantity;
        this.minStockThreshold = minStockThreshold;
        this.batchCode = batchCode;
        this.expirationDate = expirationDate;
        this.weightKg = weightKg;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSku() { return sku; }
    public void setSku(String sku) { this.sku = sku; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getNutritionalInfo() { return nutritionalInfo; }
    public void setNutritionalInfo(String nutritionalInfo) { this.nutritionalInfo = nutritionalInfo; }

    public String getTargetGoal() { return targetGoal; }
    public void setTargetGoal(String targetGoal) { this.targetGoal = targetGoal; }

    public Integer getStockQuantity() { return stockQuantity; }
    public void setStockQuantity(Integer stockQuantity) { this.stockQuantity = stockQuantity; }

    public Integer getMinStockThreshold() { return minStockThreshold; }
    public void setMinStockThreshold(Integer minStockThreshold) { this.minStockThreshold = minStockThreshold; }

    public String getBatchCode() { return batchCode; }
    public void setBatchCode(String batchCode) { this.batchCode = batchCode; }

    public LocalDate getExpirationDate() { return expirationDate; }
    public void setExpirationDate(LocalDate expirationDate) { this.expirationDate = expirationDate; }

    public Double getWeightKg() { return weightKg != null ? weightKg : 0.5; }
    public void setWeightKg(Double weightKg) { this.weightKg = weightKg; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }
}
