package com.fittshop.repository;

import com.fittshop.domain.model.AffiliateCommission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AffiliateCommissionRepository extends JpaRepository<AffiliateCommission, Long> {
    List<AffiliateCommission> findByAffiliateCode(String affiliateCode);
    Optional<AffiliateCommission> findByOrderNumber(String orderNumber);
}
