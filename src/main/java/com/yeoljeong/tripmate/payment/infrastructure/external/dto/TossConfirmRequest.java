package com.yeoljeong.tripmate.payment.infrastructure.external.dto;

import java.math.BigDecimal;

public record TossConfirmRequest(
        String paymentKey,
        String orderId,
        BigDecimal amount
) {
    public static TossConfirmRequest of(String paymentKey, String tossOrderId, BigDecimal amount) {
        return new TossConfirmRequest(paymentKey, tossOrderId, amount);
    }
}
