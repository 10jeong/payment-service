package com.yeoljeong.tripmate.payment.application.dto.result;

import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import lombok.Builder;

import java.math.BigDecimal;
import java.util.UUID;

@Builder
public record GetPaymentResult(
        UUID paymentId,
        UUID orderId,
        String tossOrderId,
        PaymentStatus paymentStatus,
        BigDecimal amount
)  {
    public static GetPaymentResult from(Payment payment) {
        return GetPaymentResult.builder()
                .paymentId(payment.getId())
                .orderId(payment.getOrderId())
                .tossOrderId(payment.getTossPayment().getTossOrderId())
                .paymentStatus(payment.getPaymentStatus())
                .amount(payment.getPaymentAmount().getRequestedAmount())
                .build();
    }
}
