package com.fittshop.controller;

import com.fittshop.domain.dto.InventoryUpdateDto;
import com.fittshop.domain.model.InventoryAuditLog;
import com.fittshop.domain.model.Product;
import com.fittshop.service.InventoryService;
import com.fittshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Estoque & Auditoria DevSecOps", description = "Endpoints de gestão de estoque, reposição de lotes e trilha de auditoria")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;
    private final ProductService productService;

    public InventoryController(InventoryService inventoryService, ProductService productService) {
        this.inventoryService = inventoryService;
        this.productService = productService;
    }

    @PostMapping("/adjust")
    @Operation(summary = "Processa entrada ou ajuste de lote no estoque com auditoria criptográfica SHA-256")
    public ResponseEntity<Product> adjustInventory(@Valid @RequestBody InventoryUpdateDto dto) {
        return ResponseEntity.ok(inventoryService.processInventoryAdjustment(dto));
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Retorna histórico imutável de movimentações de estoque para auditoria")
    public ResponseEntity<List<InventoryAuditLog>> getAuditLogs() {
        return ResponseEntity.ok(inventoryService.getAuditLogs());
    }

    @GetMapping("/low-stock")
    @Operation(summary = "Alerta de estoque baixo para reposição imediata")
    public ResponseEntity<List<Product>> getLowStockAlerts() {
        return ResponseEntity.ok(productService.getLowStockAlerts());
    }
}
