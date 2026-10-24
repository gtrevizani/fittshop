package com.fittshop.repository;

import com.fittshop.domain.model.StockReservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface StockReservationRepository extends JpaRepository<StockReservation, Long> {

    List<StockReservation> findByReservationUuid(String reservationUuid);

    @Query("SELECT COALESCE(SUM(r.quantity), 0) FROM StockReservation r WHERE r.productId = :productId AND r.status = 'ACTIVE' AND r.expiresAt > :now")
    Integer getActiveReservedQuantity(@Param("productId") Long productId, @Param("now") LocalDateTime now);

    @Query("SELECT r FROM StockReservation r WHERE r.status = 'ACTIVE' AND r.expiresAt <= :now")
    List<StockReservation> findExpiredReservations(@Param("now") LocalDateTime now);

    List<StockReservation> findByProductIdAndStatus(Long productId, String status);
}
