package com.yeoljeong.tripmate.payment.infrastructure.external;

import com.yeoljeong.tripmate.payment.infrastructure.config.TossPaymentFeignConfig;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossConfirmRequest;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossConfirmResponse;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossRefundRequest;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossRefundResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "tossPaymentFeignClient", url = "${toss.payments.base-url}", configuration = TossPaymentFeignConfig.class)
public interface TossPaymentFeignClient {

    @PostMapping("/v1/payments/confirm")
    TossConfirmResponse confirm(@RequestBody TossConfirmRequest request);

    @PostMapping("/v1/payments/{paymentKey}/cancel")
    TossRefundResponse refundPayment(@PathVariable("paymentKey") String paymentKey, @RequestBody TossRefundRequest request);
}
