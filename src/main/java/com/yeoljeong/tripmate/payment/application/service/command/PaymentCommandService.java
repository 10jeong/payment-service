package com.yeoljeong.tripmate.payment.application.service.command;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.client.OrderClient;
import com.yeoljeong.tripmate.payment.application.dto.command.PayableCommand;
import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;

    private static final String SUCCESS_URL = "http://localhost:8080/api/payments/success";
    private static final String FAIL_URL = "http://localhost:8080/api/payments/fail";

    @Transactional
    public CreatePaymentResult createPayment(UUID userId, UUID orderId) {
        PayableCommand payableCommand = orderClient.getOrderPayment(orderId);

        validateOrderOwner(userId, payableCommand);
        validatePayableOrder(payableCommand);

        String tossOrderId = generateTossOrderId(payableCommand.orderId());

        Payment payment = Payment.create(userId, payableCommand.orderId(), tossOrderId,
                payableCommand.amount(), Instant.now());

        Payment savedPayment = paymentRepository.save(payment);

        return CreatePaymentResult.of(savedPayment, payableCommand.orderName(), SUCCESS_URL, FAIL_URL);
    }

    private String generateTossOrderId(UUID orderId) {
        String shortOrderId = orderId.toString().replace("-", "").substring(0, 16);
        return "ORD-" + shortOrderId + "-" + System.currentTimeMillis();
    }

    private void validateOrderOwner(UUID userId, PayableCommand order) {
        if (!userId.equals(order.userId())) {
            throw new BusinessException(PaymentErrorCode.ORDER_OWNER_MISMATCH);
        }
    }

    private void validatePayableOrder(PayableCommand order) {
        if (!"CREATED".equals(order.orderStatus())) {
            throw new BusinessException(PaymentErrorCode.ORDER_NOT_PAYABLE);
        }
    }
}
