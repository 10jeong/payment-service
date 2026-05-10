package com.yeoljeong.tripmate.payment.application.dto.result;

public record PaymentExistenceResult(
        boolean exists,
        String paymentStatus
) {}
