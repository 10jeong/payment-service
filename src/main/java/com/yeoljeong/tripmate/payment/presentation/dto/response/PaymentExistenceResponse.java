package com.yeoljeong.tripmate.payment.presentation.dto.response;

public record PaymentExistenceResponse(
        boolean exists,
        String paymentStatus
) {}
