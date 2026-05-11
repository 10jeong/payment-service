package com.yeoljeong.tripmate.payment.presentation.dto.request;

import java.math.BigDecimal;

public record ConfirmPaymentRequest(
        String paymentKey,
        String tossOrderId,
        BigDecimal amount
) {}
