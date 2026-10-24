package com.fittshop.service;

import com.fittshop.domain.model.Product;
import com.fittshop.repository.ProductRepository;
import com.fittshop.repository.StockReservationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;
    private final StockReservationRepository stockReservationRepository;

    public ProductService(ProductRepository productRepository, StockReservationRepository stockReservationRepository) {
        this.productRepository = productRepository;
        this.stockReservationRepository = stockReservationRepository;
    }

    public List<Product> findAll() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public List<Product> findByCategory(String category) {
        return productRepository.findByCategoryIgnoreCase(category);
    }

    public List<Product> findByTargetGoal(String targetGoal) {
        return productRepository.findByTargetGoal(targetGoal);
    }

    public List<Product> search(String query) {
        if (query == null || query.isBlank()) {
            return productRepository.findAll();
        }
        return productRepository.searchProducts(query.trim());
    }

    public int getAvailableStock(Long productId) {
        Product p = productRepository.findById(productId).orElse(null);
        if (p == null) return 0;
        int reserved = stockReservationRepository.getActiveReservedQuantity(productId, LocalDateTime.now());
        return Math.max(0, p.getStockQuantity() - reserved);
    }

    @Transactional
    public Product save(Product product) {
        return productRepository.save(product);
    }

    public List<Product> getLowStockAlerts() {
        return productRepository.findLowStockProducts();
    }
}
