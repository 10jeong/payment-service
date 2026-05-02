package com.yeoljeong.tripmate.payment.application.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentCompletedEvent(
        UUID eventId,
        UUID userId,
        UUID orderId,
        UUID paymentId,
        String productName,
        BigDecimal paymentPrice,
        Instant paymentDateTime,
        String paymentMethod
) { }
