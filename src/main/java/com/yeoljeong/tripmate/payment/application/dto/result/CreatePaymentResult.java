package com.yeoljeong.tripmate.payment.application.dto.result;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

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
        return new CreatePaymentResult(
                payment.getId(),
                payment.getOrderId(),
                payment.getTossPayment().getTossOrderId(),
                payment.getPaymentStatus(),
                payment.getPaymentAmount().getRequestedAmount(),
                orderName,
                successUrl,
                failUrl,
                payment.getPaymentTimestamps().getRequestedAt()
        );
    }
}
