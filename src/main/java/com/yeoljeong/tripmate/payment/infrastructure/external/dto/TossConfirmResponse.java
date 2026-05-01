package com.yeoljeong.tripmate.payment.infrastructure.external.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record TossConfirmResponse(
        String paymentKey,
        String orderId,
        String status,
        String method,
        BigDecimal totalAmount,
        Instant approvedAt,
        Receipt receipt
) {
    public String receiptUrl() {
        return receipt == null ? null : receipt.url();
    }

    public record Receipt(
            String url
    ) { }
}
