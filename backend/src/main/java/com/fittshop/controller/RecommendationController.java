package com.fittshop.controller;

import com.fittshop.domain.dto.AthleteProfileRequest;
import com.fittshop.domain.dto.RecommendationResponse;
import com.fittshop.service.RecommendationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recommendations")
@Tag(name = "Consultor NutriFit IA", description = "Motor de recomendação personalizada por objetivo atlético")
@CrossOrigin(origins = "*")
public class RecommendationController {

    private final RecommendationService recommendationService;

    public RecommendationController(RecommendationService recommendationService) {
        this.recommendationService = recommendationService;
    }

    @PostMapping("/calculate")
    @Operation(summary = "Calcula plano nutricional, doses e combo sugerido com desconto especial")
    public ResponseEntity<RecommendationResponse> calculatePlan(@Valid @RequestBody AthleteProfileRequest request) {
        return ResponseEntity.ok(recommendationService.generatePlan(request));
    }
}
