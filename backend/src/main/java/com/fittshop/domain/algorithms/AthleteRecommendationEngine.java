package com.fittshop.domain.algorithms;

import com.fittshop.domain.enums.FitnessGoal;
import com.fittshop.domain.model.AthleteProfile;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class AthleteRecommendationEngine {

    public static class RecommendationPlan {
        private final FitnessGoal goal;
        private final double dailyProteinGrams;
        private final double dailyCreatineGrams;
        private final double estimatedWaterLiters;
        private final List<String> recommendedCategories;
        private final List<String> primarySupplements;
        private final List<String> recommendedGear;
        private final double comboDiscountPercentage;
        private final String rationalExplanation;

        public RecommendationPlan(FitnessGoal goal, double dailyProteinGrams, double dailyCreatineGrams,
                                  double estimatedWaterLiters, List<String> recommendedCategories,
                                  List<String> primarySupplements, List<String> recommendedGear,
                                  double comboDiscountPercentage, String rationalExplanation) {
            this.goal = goal;
            this.dailyProteinGrams = dailyProteinGrams;
            this.dailyCreatineGrams = dailyCreatineGrams;
            this.estimatedWaterLiters = estimatedWaterLiters;
            this.recommendedCategories = recommendedCategories;
            this.primarySupplements = primarySupplements;
            this.recommendedGear = recommendedGear;
            this.comboDiscountPercentage = comboDiscountPercentage;
            this.rationalExplanation = rationalExplanation;
        }

        public FitnessGoal getGoal() { return goal; }
        public double getDailyProteinGrams() { return dailyProteinGrams; }
        public double getDailyCreatineGrams() { return dailyCreatineGrams; }
        public double getEstimatedWaterLiters() { return estimatedWaterLiters; }
        public List<String> getRecommendedCategories() { return recommendedCategories; }
        public List<String> getPrimarySupplements() { return primarySupplements; }
        public List<String> getRecommendedGear() { return recommendedGear; }
        public double getComboDiscountPercentage() { return comboDiscountPercentage; }
        public String getRationalExplanation() { return rationalExplanation; }
    }

    public RecommendationPlan calculate(AthleteProfile profile) {
        double weight = (profile != null && profile.getWeightKg() != null && profile.getWeightKg() > 0)
                ? profile.getWeightKg() : 75.0;
        FitnessGoal goal = (profile != null && profile.getGoal() != null)
                ? profile.getGoal() : FitnessGoal.HIPERTROFIA;

        double proteinPerKg;
        double creatineGrams = 5.0; // Padrão ouro ergogênico ANVISA
        double waterLiters = weight * 0.045; // 45ml por kg para atletas
        List<String> categories = new ArrayList<>();
        List<String> supplements = new ArrayList<>();
        List<String> gear = new ArrayList<>();
        String rationale;

        switch (goal) {
            case HIPERTROFIA:
                proteinPerKg = 2.0;
                categories.add("Suplementos");
                categories.add("Combos");
                categories.add("Acessórios");
                supplements.add("100% Whey Protein Concentrado/Isolado");
                supplements.add("Creatina Creapure 300g 100% Pura");
                supplements.add("Pré-Treino Venom Extreme");
                gear.add("Shaker Térmico Inox 700ml");
                gear.add("Straps de Algodão Heavy Duty");
                rationale = "Para síntese proteica máxima e ganho de massa magra, seu corpo necessita de superávit calórico controlado e aporte de 2.0g/kg de proteína diária somado à saturação de creatina fosfato.";
                break;

            case DEFINICAO:
                proteinPerKg = 2.2; // Maior retenção muscular em déficit calórico
                categories.add("Suplementos");
                categories.add("Roupas");
                supplements.add("100% Whey Isolate Zero Carbo");
                supplements.add("Termogênico Cafeína Anidra 420mg");
                supplements.add("Glutamina Micronizada");
                gear.add("Camiseta Dry-Fit Gym Performance");
                gear.add("Shorts de Compressão Anti-Atrito");
                rationale = "Em fase de queima de gordura e déficit calórico, o aporte proteico deve subir para 2.2g/kg para blindar a massa magra contra o catabolismo, aliado a termogênicos de alta pureza.";
                break;

            case FORCA_POWERLIFTING:
                proteinPerKg = 1.8;
                categories.add("Acessórios");
                categories.add("Suplementos");
                supplements.add("Creatina Monohidratada 1000g");
                supplements.add("Beta-Alanina Pura (Tampão de Ácido Lático)");
                supplements.add("Multivitamínico High Potency");
                gear.add("Cinturão de Couro Legítimo 10mm");
                gear.add("Munhequeiras Elásticas Pro 50cm");
                gear.add("Joelheiras de Neoprene 7mm");
                rationale = "Para suportar cargas extremas e cargas neurais intensas, a estabilização intra-abdominal com cinturão de 10mm e tamponamento intracelular com beta-alanina e creatina são cruciais.";
                break;

            case RESISTENCIA_CARDIO:
            default:
                proteinPerKg = 1.6;
                categories.add("Suplementos");
                categories.add("Acessórios");
                supplements.add("Isotônico Eletrolítico com Palatinose");
                supplements.add("BCAA 8:1:1 Recovery");
                supplements.add("Whey Hidrolisado Ultra Rápido");
                gear.add("Galão de Água 2.2 Litros Bisfenol Free");
                gear.add("Meias de Compressão Graduada");
                rationale = "Para treinos de alta duração e endurance, a reposição hidroeletrolítica imediata e aporte de carboidratos de baixo índice glicêmico sustentam o rendimento cardiovascular.";
                break;
        }

        double totalDailyProtein = Math.round(weight * proteinPerKg * 10.0) / 10.0;
        double roundedWater = Math.round(waterLiters * 10.0) / 10.0;

        return new RecommendationPlan(
                goal,
                totalDailyProtein,
                creatineGrams,
                roundedWater,
                categories,
                supplements,
                gear,
                0.15, // 15% de desconto no combo montado
                rationale
        );
    }
}
