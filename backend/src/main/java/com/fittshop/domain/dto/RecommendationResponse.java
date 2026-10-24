package com.fittshop.domain.dto;

import com.fittshop.domain.enums.FitnessGoal;
import com.fittshop.domain.model.Product;
import java.math.BigDecimal;
import java.util.List;

public class RecommendationResponse {

    private FitnessGoal goal;
    private double dailyProteinGrams;
    private double dailyCreatineGrams;
    private double estimatedWaterLiters;
    private String rationale;
    private double comboDiscountPercentage;

    private List<Product> recommendedProducts;
    private BigDecimal regularTotalPrice;
    private BigDecimal comboDiscountedPrice;
    private BigDecimal totalSavings;

    public RecommendationResponse() {}

    public FitnessGoal getGoal() { return goal; }
    public void setGoal(FitnessGoal goal) { this.goal = goal; }

    public double getDailyProteinGrams() { return dailyProteinGrams; }
    public void setDailyProteinGrams(double dailyProteinGrams) { this.dailyProteinGrams = dailyProteinGrams; }

    public double getDailyCreatineGrams() { return dailyCreatineGrams; }
    public void setDailyCreatineGrams(double dailyCreatineGrams) { this.dailyCreatineGrams = dailyCreatineGrams; }

    public double getEstimatedWaterLiters() { return estimatedWaterLiters; }
    public void setEstimatedWaterLiters(double estimatedWaterLiters) { this.estimatedWaterLiters = estimatedWaterLiters; }

    public String getRationale() { return rationale; }
    public void setRationale(String rationale) { this.rationale = rationale; }

    public double getComboDiscountPercentage() { return comboDiscountPercentage; }
    public void setComboDiscountPercentage(double comboDiscountPercentage) { this.comboDiscountPercentage = comboDiscountPercentage; }

    public List<Product> getRecommendedProducts() { return recommendedProducts; }
    public void setRecommendedProducts(List<Product> recommendedProducts) { this.recommendedProducts = recommendedProducts; }

    public BigDecimal getRegularTotalPrice() { return regularTotalPrice; }
    public void setRegularTotalPrice(BigDecimal regularTotalPrice) { this.regularTotalPrice = regularTotalPrice; }

    public BigDecimal getComboDiscountedPrice() { return comboDiscountedPrice; }
    public void setComboDiscountedPrice(BigDecimal comboDiscountedPrice) { this.comboDiscountedPrice = comboDiscountedPrice; }

    public BigDecimal getTotalSavings() { return totalSavings; }
    public void setTotalSavings(BigDecimal totalSavings) { this.totalSavings = totalSavings; }
}
