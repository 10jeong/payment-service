package com.yeoljeong.tripmate.payment.infrastructure.config;

import com.yeoljeong.tripmate.payment.application.properties.TossPaymentProperties;
import feign.RequestInterceptor;
import org.springframework.context.annotation.Bean;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TossPaymentFeignConfig {

    @Bean
    public RequestInterceptor tossPaymentRequestInterceptor(TossPaymentProperties tossPaymentProperties) {
        return template -> {
            String secretKey = tossPaymentProperties.secretKey() + ":";
            String encodedSecretKey = Base64.getEncoder()
                    .encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));

            template.header("Authorization", "Basic " + encodedSecretKey);
            template.header("Content-Type", "application/json");
        };
    }
}
