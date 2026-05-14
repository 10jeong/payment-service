package com.yeoljeong.tripmate.payment.presentation.controller.external;

import com.yeoljeong.tripmate.auth.annotation.LoginUser;
import com.yeoljeong.tripmate.auth.context.UserContext;
import com.yeoljeong.tripmate.payment.application.dto.command.ConfirmPaymentCommand;
import com.yeoljeong.tripmate.payment.application.dto.result.ConfirmPaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.GetPaymentResult;
import com.yeoljeong.tripmate.payment.application.service.command.PaymentCommandService;
import com.yeoljeong.tripmate.payment.application.service.query.PaymentQueryService;
import com.yeoljeong.tripmate.payment.presentation.dto.request.ConfirmPaymentRequest;
import com.yeoljeong.tripmate.payment.presentation.dto.request.CreatePaymentRequest;
import com.yeoljeong.tripmate.payment.presentation.dto.response.ConfirmPaymentResponse;
import com.yeoljeong.tripmate.payment.presentation.dto.response.CreatePaymentResponse;
import com.yeoljeong.tripmate.payment.presentation.dto.response.GetPaymentResponse;
import com.yeoljeong.tripmate.response.ApiResponse;
import com.yeoljeong.tripmate.response.constants.CommonSuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentCommandService commandService;
    private final PaymentQueryService queryService;

    @PostMapping
    public ApiResponse<CreatePaymentResponse> createPayment(@LoginUser UserContext userContext, @RequestBody CreatePaymentRequest request) throws NoSuchAlgorithmException {
        CreatePaymentResult result = commandService.createPayment(userContext.userId(), request.orderId());

        return ApiResponse.success(CommonSuccessCode.OK, CreatePaymentResponse.from(result));
    }

    @PostMapping("/confirm")
    public ApiResponse<ConfirmPaymentResponse> confirmPayment(@LoginUser UserContext userContext, @RequestBody ConfirmPaymentRequest request) throws NoSuchAlgorithmException {
        ConfirmPaymentCommand command = new ConfirmPaymentCommand(request.paymentKey(), request.tossOrderId(), request.amount());
        ConfirmPaymentResult result = commandService.confirmPayment(userContext.userId(), command);

        return ApiResponse.success(CommonSuccessCode.OK, ConfirmPaymentResponse.from(result));
    }

    @GetMapping("/{paymentId}")
    public ApiResponse<GetPaymentResponse> getOrder(@LoginUser UserContext userContext, @PathVariable("paymentId") UUID paymentId) {
        GetPaymentResult result = queryService.getPayment(paymentId, userContext.userId());
        return ApiResponse.success(CommonSuccessCode.OK, GetPaymentResponse.from(result));
    }
}
