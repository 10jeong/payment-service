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
        String receipt
) {}
