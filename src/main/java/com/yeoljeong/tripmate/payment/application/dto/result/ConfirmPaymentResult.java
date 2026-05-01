package com.yeoljeong.tripmate.payment.application.dto.result;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record ConfirmPaymentResult(
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
    public static ConfirmPaymentResult of(UUID paymentId, UUID orderId, String tossOrderId, String paymentKey,
                                          PaymentStatus paymentStatus, String paymentMethod, BigDecimal requestedAmount, BigDecimal approvedAmount,
                                          String receiptUrl, Instant approvedAt) {
        return ConfirmPaymentResult.builder()
                .paymentId(paymentId)
                .orderId(orderId)
                .tossOrderId(tossOrderId)
                .paymentKey(paymentKey)
                .paymentStatus(paymentStatus)
                .paymentMethod(paymentMethod)
                .requestedAmount(requestedAmount)
                .approvedAmount(approvedAmount)
                .receiptUrl(receiptUrl)
                .approvedAt(approvedAt)
                .build();
    }

}
