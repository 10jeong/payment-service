package com.yeoljeong.tripmate.payment.infrastructure.external;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.client.OrderClient;
import com.yeoljeong.tripmate.payment.application.dto.command.PayableCommand;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import com.yeoljeong.tripmate.payment.infrastructure.external.dto.OrderPaymentResponse;
import feign.FeignException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class OrderAdapter implements OrderClient {

    private final OrderFeignClient orderFeignClient;

    @Override
    public PayableCommand getOrderPayment(UUID orderId) {
        try {
            OrderPaymentResponse orderPaymentResponse = orderFeignClient.getOrderPayment(orderId);

            if (orderPaymentResponse == null) {
                throw new BusinessException(PaymentErrorCode.ORDER_PAYMENT_NOT_FOUND);
            }

            return new PayableCommand(orderPaymentResponse.orderId(), orderPaymentResponse.userId(), orderPaymentResponse.orderName(),
                    orderPaymentResponse.amount(), orderPaymentResponse.orderStatus());
        } catch (FeignException.NotFound e) {
            throw new BusinessException(PaymentErrorCode.ORDER_PAYMENT_NOT_FOUND);

        } catch (FeignException e) {
            throw new BusinessException(PaymentErrorCode.ORDER_SERVICE_ERROR);
        }
    }
}
