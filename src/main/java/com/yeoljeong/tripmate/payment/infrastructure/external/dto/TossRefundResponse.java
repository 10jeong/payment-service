package com.yeoljeong.tripmate.payment.infrastructure.external.dto;

public record TossRefundResponse(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount
) { }
