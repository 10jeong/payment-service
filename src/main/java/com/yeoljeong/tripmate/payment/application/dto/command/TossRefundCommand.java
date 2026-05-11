package com.yeoljeong.tripmate.payment.application.dto.command;

public record TossRefundCommand(
        String paymentKey,
        String orderId,
        String status,
        Long totalAmount
) {
    public boolean isCanceled() {
        return "CANCELED".equals(status);
    }
}
