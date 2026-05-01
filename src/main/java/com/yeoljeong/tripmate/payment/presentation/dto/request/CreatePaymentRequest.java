package com.yeoljeong.tripmate.payment.presentation.dto.request;

import java.util.UUID;

public record CreatePaymentRequest(
        UUID orderId
) {}
