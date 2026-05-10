package com.yeoljeong.tripmate.payment.presentation.controller.internal;

import com.yeoljeong.tripmate.payment.application.service.query.PaymentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/payments")
public class PaymentInternalController {

    private final PaymentQueryService queryService;

}
