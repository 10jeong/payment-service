package com.yeoljeong.tripmate.payment.infrastructure.external;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.client.TossPaymentClient;
import com.yeoljeong.tripmate.payment.application.dto.command.TossConfirmCommand;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossConfirmRequest;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.TossConfirmResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
@RequiredArgsConstructor
public class TossPaymentAdapter implements TossPaymentClient {

    private final TossPaymentFeignClient tossPaymentFeignClient;

    @Override
    public TossConfirmCommand confirm(String paymentKey, String orderId, BigDecimal amount) {

        try {
            TossConfirmResponse tossConfirmResponse = tossPaymentFeignClient.confirm(TossConfirmRequest.of(paymentKey, orderId,amount));

            if (tossConfirmResponse == null) {
                throw new BusinessException(PaymentErrorCode.TOSS_RESPONSE_NOT_FOUND);
            }

            return new TossConfirmCommand(tossConfirmResponse.paymentKey(), tossConfirmResponse.orderId(), tossConfirmResponse.status(),
                    tossConfirmResponse.method(), tossConfirmResponse.totalAmount(), tossConfirmResponse.approvedAt(), tossConfirmResponse.receiptUrl());

        } catch (FeignException.NotFound e) {
            throw new BusinessException(PaymentErrorCode.TOSS_RESPONSE_NOT_FOUND);

        } catch (FeignException e) {
            if (e.status() >= 400 && e.status() < 500) {
                throw new BusinessException(PaymentErrorCode.TOSS_PAYMENT_CLIENT_ERROR);
            }

            throw new BusinessException(PaymentErrorCode.TOSS_PAYMENT_ERROR);
        }
    }
}
