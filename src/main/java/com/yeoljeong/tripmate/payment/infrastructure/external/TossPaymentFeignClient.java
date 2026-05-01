package com.yeoljeong.tripmate.payment.infrastructure.external;

import com.yeoljeong.tripmate.payment.infrastructure.config.TossPaymentFeignConfig;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossConfirmRequest;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossConfirmResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tossPaymentFeignClient", url = "${payment.toss.base-url}", configuration = TossPaymentFeignConfig.class)
public interface TossPaymentFeignClient {

    @PostMapping("/v1/payments/confirm")
    TossConfirmResponse confirm(@RequestBody TossConfirmRequest request);
}
