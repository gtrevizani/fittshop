package com.fittshop.repository;

import com.fittshop.domain.model.InventoryAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface InventoryAuditLogRepository extends JpaRepository<InventoryAuditLog, Long> {

    List<InventoryAuditLog> findTop50ByOrderByCreatedAtDesc();

    List<InventoryAuditLog> findByProductIdOrderByCreatedAtDesc(Long productId);
}
