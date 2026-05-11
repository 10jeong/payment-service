package com.yeoljeong.tripmate.payment.application.dto.command;

import java.math.BigDecimal;

public record ConfirmPaymentCommand(
        String paymentKey,
        String tossOrderId,
        BigDecimal amount
) { }
