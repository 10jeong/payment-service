package com.yeoljeong.tripmate.payment.infrastructure.external;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yeoljeong.tripmate.payment.application.client.TossPaymentClient;
import com.yeoljeong.tripmate.payment.application.dto.command.TossConfirmCommand;
import com.yeoljeong.tripmate.payment.application.dto.command.TossRefundCommand;
import com.yeoljeong.tripmate.payment.application.exception.ExternalPaymentException;
import com.yeoljeong.tripmate.payment.application.exception.ExternalPaymentFailureReason;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.*;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TossPaymentAdapter implements TossPaymentClient {

    private final TossPaymentFeignClient tossPaymentFeignClient;
    private final ObjectMapper objectMapper;

    @Override
    public TossConfirmCommand confirm(String paymentKey, String orderId, BigDecimal amount) {

        return new TossConfirmCommand(paymentKey, orderId, "DONE",
                "CARD", amount, Instant.now(), "https://mock-receipt.test/" + paymentKey);

//        try {
//            TossConfirmResponse tossConfirmResponse = tossPaymentFeignClient.confirm(TossConfirmRequest.of(paymentKey, orderId,amount));
//
//            if (tossConfirmResponse == null) {
//                throw new ExternalPaymentException(ExternalPaymentFailureReason.NOT_FOUND, "토스 결제 승인 응답이 존재하지 않습니다.");
//            }
//
//            return new TossConfirmCommand(tossConfirmResponse.paymentKey(), tossConfirmResponse.orderId(), tossConfirmResponse.status(),
//                    tossConfirmResponse.method(), tossConfirmResponse.totalAmount(), tossConfirmResponse.approvedAt(), tossConfirmResponse.receiptUrl());
//
//        } catch (FeignException.NotFound e) {
//            throw new ExternalPaymentException(ExternalPaymentFailureReason.NOT_FOUND, "토스 결제 정보를 찾을 수 없습니다.");
//
//        } catch (FeignException e) {
//            throw convertTossException(e);
//        }
    }

    @Override
    public TossRefundCommand refundPayment(String paymentKey, String cancelReason) {

        return new TossRefundCommand(paymentKey, UUID.randomUUID().toString(),
                "CANCELLED", 0L);

//        if (cancelReason == null || cancelReason.isBlank()) {
//            throw new ExternalPaymentException(ExternalPaymentFailureReason.CLIENT_ERROR, "취소 사유는 필수입니다.");
//        }

//        try {
//            TossRefundResponse tossRefundResponse = tossPaymentFeignClient.refundPayment(paymentKey, new TossRefundRequest(cancelReason));
//
//            if (tossRefundResponse == null) {
//                throw new ExternalPaymentException(ExternalPaymentFailureReason.NOT_FOUND, "토스 환불 응답이 존재하지 않습니다.");
//            }
//
//            return new TossRefundCommand(tossRefundResponse.paymentKey(), tossRefundResponse.orderId(),
//                    tossRefundResponse.status(), tossRefundResponse.totalAmount());
//        } catch (FeignException.NotFound e) {
//            throw new ExternalPaymentException(ExternalPaymentFailureReason.NOT_FOUND, "토스 결제 정보를 찾을 수 없습니다.");
//
//        } catch (FeignException e) {
//            throw convertTossException(e);
//        }
    }

    // Toss 에러 응답에 따른 처리
    private ExternalPaymentException convertTossException(FeignException e) {
        TossErrorResponse errorResponse = parseTossErrorResponse(e);

        // 이미 처리된 결제에 대해 다시 승인 요청을 호출한 경우(이미 처리된 결제 입니다.)
        if (errorResponse != null && "ALREADY_PROCESSED_PAYMENT".equals(errorResponse.code())) {
            return new ExternalPaymentException(ExternalPaymentFailureReason.ALREADY_PROCESSED, errorResponse.message());
        }

        // 400번대 에러
        if (e.status() >= 400 && e.status() < 500) {
            return new ExternalPaymentException(ExternalPaymentFailureReason.CLIENT_ERROR,
                    errorResponse != null ? errorResponse.message() : "토스 결제 요청이 올바르지 않습니다.");
        }

        return new ExternalPaymentException(ExternalPaymentFailureReason.PROVIDER_ERROR,
                errorResponse != null ? errorResponse.message() : "토스 결제 서버 오류가 발생했습니다.");
    }

    private TossErrorResponse parseTossErrorResponse(FeignException e) {
        try {
            String responseBody = e.contentUTF8();

            if (responseBody == null || responseBody.isBlank()) {
                return null;
            }

            // 토스 에러 응답 JSON을 Java 객체로 변환
            return objectMapper.readValue(responseBody, TossErrorResponse.class);
        } catch (Exception ex) {
            return null;
        }
    }
}
