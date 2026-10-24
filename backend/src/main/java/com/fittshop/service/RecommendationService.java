package com.fittshop.service;

import com.fittshop.domain.algorithms.AthleteRecommendationEngine;
import com.fittshop.domain.dto.AthleteProfileRequest;
import com.fittshop.domain.dto.RecommendationResponse;
import com.fittshop.domain.model.AthleteProfile;
import com.fittshop.domain.model.Product;
import com.fittshop.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecommendationService {

    private final AthleteRecommendationEngine recommendationEngine;
    private final ProductRepository productRepository;

    public RecommendationService(AthleteRecommendationEngine recommendationEngine,
                                 ProductRepository productRepository) {
        this.recommendationEngine = recommendationEngine;
        this.productRepository = productRepository;
    }

    public RecommendationResponse generatePlan(AthleteProfileRequest request) {
        AthleteProfile profile = new AthleteProfile(
                request.getGoal(),
                request.getWeightKg(),
                request.getHeightMeters(),
                request.getWeeklyWorkouts(),
                request.getExperienceLevel()
        );

        AthleteRecommendationEngine.RecommendationPlan plan = recommendationEngine.calculate(profile);

        List<Product> products = new ArrayList<>();
        if (request.getGoal() != null) {
            products.addAll(productRepository.findByTargetGoal(request.getGoal().name()));
        }

        if (products.isEmpty()) {
            products = productRepository.findByCategoryIn(plan.getRecommendedCategories());
        }

        // Limita a 4 produtos principais para o combo montado
        List<Product> comboProducts = products.stream().limit(4).toList();

        BigDecimal regularTotal = comboProducts.stream()
                .map(Product::getPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal discountRate = BigDecimal.valueOf(plan.getComboDiscountPercentage());
        BigDecimal comboDiscounted = regularTotal.multiply(BigDecimal.ONE.subtract(discountRate)).setScale(2, RoundingMode.HALF_UP);
        BigDecimal totalSavings = regularTotal.subtract(comboDiscounted).setScale(2, RoundingMode.HALF_UP);

        RecommendationResponse response = new RecommendationResponse();
        response.setGoal(plan.getGoal());
        response.setDailyProteinGrams(plan.getDailyProteinGrams());
        response.setDailyCreatineGrams(plan.getDailyCreatineGrams());
        response.setEstimatedWaterLiters(plan.getEstimatedWaterLiters());
        response.setRationale(plan.getRationalExplanation());
        response.setComboDiscountPercentage(plan.getComboDiscountPercentage());
        response.setRecommendedProducts(comboProducts);
        response.setRegularTotalPrice(regularTotal);
        response.setComboDiscountedPrice(comboDiscounted);
        response.setTotalSavings(totalSavings);

        return response;
    }
}
