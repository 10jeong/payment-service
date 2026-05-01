package com.yeoljeong.tripmate.payment.application.dto.command;

import java.math.BigDecimal;
import java.time.Instant;

public record TossConfirmCommand(
        String paymentKey,
        String orderId,
        String status,
        String method,
        BigDecimal totalAmount,
        Instant approvedAt,
        String receiptUrl
) { }
