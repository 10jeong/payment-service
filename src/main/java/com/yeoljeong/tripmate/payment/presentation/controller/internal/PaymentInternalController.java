package com.yeoljeong.tripmate.payment.presentation.controller.internal;

import com.yeoljeong.tripmate.payment.application.dto.result.PaymentExistenceResult;
import com.yeoljeong.tripmate.payment.application.service.query.PaymentQueryService;
import com.yeoljeong.tripmate.payment.presentation.dto.response.PaymentExistenceResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class PaymentInternalController {

    private final PaymentQueryService queryService;

    @GetMapping("/orders/{orderId}")
    public PaymentExistenceResponse getExistencePayment(@PathVariable UUID orderId) {
        PaymentExistenceResult result = queryService.getExistencePayment(orderId);

        return new PaymentExistenceResponse(result.exists(), result.paymentStatus());
    }

}
