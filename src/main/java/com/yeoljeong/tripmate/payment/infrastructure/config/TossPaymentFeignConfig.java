package com.yeoljeong.tripmate.payment.infrastructure.config;

import com.yeoljeong.tripmate.payment.infrastructure.properties.TossPaymentProperties;
import feign.RequestInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

@Configuration
@RequiredArgsConstructor
public class TossPaymentFeignConfig {

    private static TossPaymentProperties tossPaymentProperties;

    @Bean
    public RequestInterceptor tossPaymentRequestInterceptor() {
        return template -> {
            String secretKey = tossPaymentProperties.secretKey() + ":";
            String encodedSecretKey = Base64.getEncoder()
                    .encodeToString(secretKey.getBytes(StandardCharsets.UTF_8));

            template.header("Authorization", "Basic " + encodedSecretKey);
            template.header("Content-Type", "application/json");
        };
    }
}
