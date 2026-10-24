package com.fittshop.domain.algorithms;

import com.fittshop.domain.model.Product;
import com.fittshop.domain.model.StockReservation;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.UUID;

@Component
public class StockReservationEngine {

    public static class ReservationEvaluation {
        private final boolean success;
        private final String message;
        private final StockReservation reservation;
        private final int availableStock;

        public ReservationEvaluation(boolean success, String message, StockReservation reservation, int availableStock) {
            this.success = success;
            this.message = message;
            this.reservation = reservation;
            this.availableStock = availableStock;
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public StockReservation getReservation() { return reservation; }
        public int getAvailableStock() { return availableStock; }
    }

    public ReservationEvaluation evaluateAndCreate(Product product, int activeReservedQuantity,
                                                   int requestedQuantity, int leaseTtlSeconds) {
        if (product == null) {
            return new ReservationEvaluation(false, "Produto não encontrado.", null, 0);
        }

        int physicalStock = product.getStockQuantity() != null ? product.getStockQuantity() : 0;
        int availableStock = physicalStock - activeReservedQuantity;

        if (requestedQuantity <= 0) {
            return new ReservationEvaluation(false, "Quantidade solicitada deve ser maior que zero.", null, availableStock);
        }

        if (requestedQuantity > availableStock) {
            return new ReservationEvaluation(
                    false,
                    String.format("Estoque insuficiente para o produto '%s'. Disponível: %d, Solicitado: %d",
                            product.getName(), availableStock, requestedQuantity),
                    null,
                    availableStock
            );
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(leaseTtlSeconds);

        StockReservation reservation = new StockReservation(
                UUID.randomUUID().toString(),
                product.getId(),
                requestedQuantity,
                now,
                expiresAt,
                "ACTIVE"
        );

        return new ReservationEvaluation(true, "Reserva de estoque efetuada com sucesso.", reservation, availableStock - requestedQuantity);
    }
}
