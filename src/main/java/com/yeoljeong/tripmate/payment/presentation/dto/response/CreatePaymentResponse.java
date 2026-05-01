package com.yeoljeong.tripmate.payment.presentation.dto.response;

import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.util.UUID;

public record CreatePaymentResponse(
        UUID paymentId,
        UUID orderId,
        String tossOrderId,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String orderName,
        String successUrl,
        String failUrl
) {
    public static CreatePaymentResponse from(CreatePaymentResult result) {
        return new CreatePaymentResponse(
                result.paymentId(),
                result.orderId(),
                result.tossOrderId(),
                result.paymentStatus(),
                result.amount(),
                result.orderName(),
                result.successUrl(),
                result.failUrl()
        );
    }
}
