package com.yeoljeong.tripmate.payment.presentation.controller;

import com.yeoljeong.tripmate.payment.application.dto.command.ConfirmPaymentCommand;
import com.yeoljeong.tripmate.payment.application.dto.result.ConfirmPaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.payment.application.service.command.PaymentCommandService;
import com.yeoljeong.tripmate.payment.presentation.dto.request.ConfirmPaymentRequest;
import com.yeoljeong.tripmate.payment.presentation.dto.request.CreatePaymentRequest;
import com.yeoljeong.tripmate.payment.presentation.dto.response.ConfirmPaymentResponse;
import com.yeoljeong.tripmate.payment.presentation.dto.response.CreatePaymentResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentCommandService commandService;

    @PostMapping
    public ApiResponse<CreatePaymentResponse> createPayment(@RequestHeader("X-User-Id") UUID userId, @RequestBody CreatePaymentRequest request) {
        CreatePaymentResult result = commandService.createPayment(userId, request.orderId());

        return ApiResponse.success(CommonSuccessCode.OK, CreatePaymentResponse.from(result));
    }

    @PostMapping("/confirm")
    public ApiResponse<ConfirmPaymentResponse> confirmPayment(@RequestHeader("X-User-Id") UUID userId, @RequestBody ConfirmPaymentRequest request) {
        ConfirmPaymentCommand command = new ConfirmPaymentCommand(request.paymentKey(), request.tossOrderId(), request.amount());
        ConfirmPaymentResult result = commandService.confirmPayment(userId, command);

        return ApiResponse.success(CommonSuccessCode.OK, ConfirmPaymentResponse.from(result));
    }
}
