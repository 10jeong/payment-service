package com.yeoljeong.tripmate.payment.infrastructure.external.dto;

import jakarta.validation.constraints.NotBlank;

public record TossRefundRequest(
        @NotBlank
        String cancelReason
) { }
