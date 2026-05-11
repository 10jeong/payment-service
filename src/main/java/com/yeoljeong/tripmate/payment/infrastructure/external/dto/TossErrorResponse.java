package com.yeoljeong.tripmate.payment.infrastructure.external.dto;

public record TossErrorResponse(
        String code,
        String message
) { }
