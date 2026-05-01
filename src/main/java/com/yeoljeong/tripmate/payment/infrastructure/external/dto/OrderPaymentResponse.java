package com.yeoljeong.tripmate.payment.infrastructure.external.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderPaymentResponse(
        UUID orderId,
        UUID userId,
        String orderName,
        BigDecimal amount,
        String orderStatus
) {
    public boolean isPayable() {
        return "CREATED".equals(orderStatus);
    }
}
