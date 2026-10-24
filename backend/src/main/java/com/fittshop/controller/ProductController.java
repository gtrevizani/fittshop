package com.fittshop.controller;

import com.fittshop.domain.model.Product;
import com.fittshop.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/products")
@Tag(name = "Produtos & Catálogo", description = "Endpoints de catálogo esportivo, suplementos e vestuário")
@CrossOrigin(origins = "*")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping
    @Operation(summary = "Lista todos os produtos ou filtra por categoria / objetivo")
    public ResponseEntity<List<Product>> getProducts(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String goal,
            @RequestParam(required = false) String search) {

        if (search != null && !search.isBlank()) {
            return ResponseEntity.ok(productService.search(search));
        }
        if (category != null && !category.isBlank()) {
            return ResponseEntity.ok(productService.findByCategory(category));
        }
        if (goal != null && !goal.isBlank()) {
            return ResponseEntity.ok(productService.findByTargetGoal(goal));
        }
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Busca detalhes de um produto por ID")
    public ResponseEntity<Product> getById(@PathVariable Long id) {
        return productService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/{id}/stock")
    @Operation(summary = "Retorna estoque disponível líquido (descontando reservas ativas)")
    public ResponseEntity<Integer> getAvailableStock(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getAvailableStock(id));
    }

    @GetMapping("/alerts/low-stock")
    @Operation(summary = "Alerta de estoque baixo para o Estoquista e Gerente")
    public ResponseEntity<List<Product>> getLowStockAlerts() {
        return ResponseEntity.ok(productService.getLowStockAlerts());
    }
}
