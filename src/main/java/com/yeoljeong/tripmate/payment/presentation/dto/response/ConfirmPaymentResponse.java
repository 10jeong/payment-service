package com.yeoljeong.tripmate.payment.presentation.dto.response;

import com.yeoljeong.tripmate.payment.application.dto.result.ConfirmPaymentResult;
import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record ConfirmPaymentResponse(
        UUID paymentId,
        UUID orderId,
        String tossOrderId,
        String paymentKey,
        PaymentStatus paymentStatus,
        String paymentMethod,
        BigDecimal requestedAmount,
        BigDecimal approvedAmount,
        String receiptUrl,
        Instant approvedAt
) {
    public static ConfirmPaymentResponse from(ConfirmPaymentResult result) {
        return new ConfirmPaymentResponse(
                result.paymentId(),
                result.orderId(),
                result.tossOrderId(),
                result.paymentKey(),
                result.paymentStatus(),
                result.paymentMethod(),
                result.requestedAmount(),
                result.approvedAmount(),
                result.receiptUrl(),
                result.approvedAt()
        );
    }
}
