package com.fittshop.service.freight;

import com.fittshop.domain.algorithms.FreightCalculationEngine.FreightResult;
import java.math.BigDecimal;

public interface FreightCarrierService {
    FreightResult calculateShipping(String destinationZipCode, BigDecimal subtotal, double totalWeightKg,
                                    BigDecimal thresholdSudeste, BigDecimal thresholdOutros);
}
