package com.fittshop.repository;

import com.fittshop.domain.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {

    Optional<Coupon> findByCodeIgnoreCaseAndActiveTrue(String code);

    Optional<Coupon> findByCodeIgnoreCase(String code);
}
