package com.fittshop.domain.algorithms;

import com.fittshop.domain.enums.FitnessGoal;
import com.fittshop.domain.model.AthleteProfile;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AthleteRecommendationEngineTest {

    private final AthleteRecommendationEngine engine = new AthleteRecommendationEngine();

    @Test
    @DisplayName("Deve calcular doses adequadas de proteína e creatina para hipertrofia")
    void shouldCalculateHypertrophyPlan() {
        AthleteProfile profile = new AthleteProfile(FitnessGoal.HIPERTROFIA, 80.0, 1.80, 5, "AVANCADO");
        AthleteRecommendationEngine.RecommendationPlan plan = engine.calculate(profile);

        assertNotNull(plan);
        assertEquals(FitnessGoal.HIPERTROFIA, plan.getGoal());
        assertEquals(160.0, plan.getDailyProteinGrams(), 0.1); // 80kg * 2.0g = 160g
        assertEquals(5.0, plan.getDailyCreatineGrams());
        assertTrue(plan.getRecommendedCategories().contains("Suplementos"));
        assertTrue(plan.getPrimarySupplements().stream().anyMatch(s -> s.contains("Whey")));
        assertEquals(0.15, plan.getComboDiscountPercentage());
    }

    @Test
    @DisplayName("Deve ajustar proteína para 2.2g/kg em fase de definição muscular")
    void shouldCalculateCuttingPlan() {
        AthleteProfile profile = new AthleteProfile(FitnessGoal.DEFINICAO, 70.0, 1.75, 6, "INTERMEDIARIO");
        AthleteRecommendationEngine.RecommendationPlan plan = engine.calculate(profile);

        assertEquals(154.0, plan.getDailyProteinGrams(), 0.1); // 70kg * 2.2g = 154g
        assertTrue(plan.getPrimarySupplements().stream().anyMatch(s -> s.contains("Termogênico") || s.contains("Isolate")));
    }
}
