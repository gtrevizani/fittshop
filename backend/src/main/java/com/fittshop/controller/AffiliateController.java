package com.fittshop.controller;

import com.fittshop.domain.dto.AffiliateCommissionSummary;
import com.fittshop.domain.dto.AffiliateValidateResponse;
import com.fittshop.service.affiliate.AffiliateService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/affiliates")
@Tag(name = "Afiliados & Personal Trainers", description = "Endpoints para validação de cupons, consulta de comissões e livro-razão de consultores")
@CrossOrigin(origins = "*")
public class AffiliateController {

    private final AffiliateService affiliateService;

    public AffiliateController(AffiliateService affiliateService) {
        this.affiliateService = affiliateService;
    }

    @GetMapping("/validate/{code}")
    @Operation(summary = "Valida código de afiliado, consultor ou personal trainer")
    public ResponseEntity<AffiliateValidateResponse> validateCode(@PathVariable String code) {
        return ResponseEntity.ok(affiliateService.validateCode(code));
    }

    @GetMapping("/{code}/statement")
    @Operation(summary = "Consulta extrato de comissões acumuladas do consultor")
    public ResponseEntity<AffiliateCommissionSummary> getStatement(@PathVariable String code) {
        return ResponseEntity.ok(affiliateService.getStatement(code));
    }
}
