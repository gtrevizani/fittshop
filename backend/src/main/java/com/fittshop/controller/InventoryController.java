package com.fittshop.controller;

import com.fittshop.domain.dto.InventoryUpdateDto;
import com.fittshop.domain.model.Product;
import com.fittshop.service.InventoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/inventory")
@Tag(name = "Operações de Estoque & Balcão", description = "Endpoints para Estoquistas (entrada de fardos, reposição) e Caixas")
@CrossOrigin(origins = "*")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    @PostMapping("/adjust")
    @Operation(summary = "Realiza reposição de fardo, ajuste ou baixa manual de estoque")
    public ResponseEntity<Product> adjustInventory(@Valid @RequestBody InventoryUpdateDto dto) {
        return ResponseEntity.ok(inventoryService.processInventoryAdjustment(dto));
    }
}
