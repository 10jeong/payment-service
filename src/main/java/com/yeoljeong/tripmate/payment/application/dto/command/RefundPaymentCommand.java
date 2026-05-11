package com.yeoljeong.tripmate.payment.application.dto.command;

import java.util.UUID;

public record RefundPaymentCommand(
        UUID orderId,
        UUID userId,
        UUID planUnitId,
        String reason,
        UUID productId,
        String productName,
        UUID scheduleId,
        int quantity
) { }
