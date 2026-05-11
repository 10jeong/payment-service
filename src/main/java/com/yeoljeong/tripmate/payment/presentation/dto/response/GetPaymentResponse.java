package com.yeoljeong.tripmate.payment.presentation.dto.response;

import com.yeoljeong.tripmate.payment.application.dto.result.GetPaymentResult;
import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record GetPaymentResponse(
        UUID paymentId,
        UUID orderId,
        String tossOrderId,
        PaymentStatus paymentStatus,
        BigDecimal amount
) {
    public static GetPaymentResponse from(GetPaymentResult result) {
        return new GetPaymentResponse(result.paymentId(), result.orderId(), result.tossOrderId(),
                result.paymentStatus(), result.amount());
    }
}
