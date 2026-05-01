package com.yeoljeong.tripmate.payment.application.dto.command;

import java.math.BigDecimal;
import java.util.UUID;

public record PayableCommand(
        UUID orderId,
        UUID userId,
        String orderName,
        BigDecimal amount,
        String orderStatus
) { }
