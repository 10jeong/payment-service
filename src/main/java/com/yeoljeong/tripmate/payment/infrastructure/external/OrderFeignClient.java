package com.yeoljeong.tripmate.payment.infrastructure.external;

import com.yeoljeong.tripmate.payment.infrastructure.external.dto.OrderPaymentResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "order-service", path = "/internal/orders")
public interface OrderFeignClient {

    @GetMapping("/{orderId}/payment")
    OrderPaymentResponse getOrderPayment(@PathVariable UUID orderId);
}
