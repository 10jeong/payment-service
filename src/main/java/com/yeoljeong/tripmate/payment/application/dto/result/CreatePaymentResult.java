package com.yeoljeong.tripmate.payment.application.dto.result;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Builder
public record CreatePaymentResult(
        UUID paymentId,
        UUID orderId,
        String tossOrderId,
        PaymentStatus paymentStatus,
        BigDecimal amount,
        String orderName,
        String successUrl,
        String failUrl,
        Instant requestedAt
) {
    public static CreatePaymentResult of(Payment payment, String orderName, String successUrl, String failUrl) {
        return CreatePaymentResult.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .tossOrderId(payment.getTossPayment().getTossOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getPaymentAmount().getRequestedAmount())
                .orderName(orderName)
                .successUrl(successUrl)
                .failUrl(failUrl)
                .requestedAt(payment.getPaymentTimestamps().getRequestedAt())
                .build();
    }
}
