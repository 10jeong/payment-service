package com.yeoljeong.tripmate.payment.infrastructure.properties;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "toss.payments")
public record TossPaymentProperties(
        String baseUrl,
        String secretKey,
        String successUrl,
        String failUrl
) { }
