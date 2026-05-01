package com.yeoljeong.tripmate.payment.application.service.command;

import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.client.OrderClient;
import com.yeoljeong.tripmate.payment.application.client.TossPaymentClient;
import com.yeoljeong.tripmate.payment.application.dto.command.ConfirmPaymentCommand;
import com.yeoljeong.tripmate.payment.application.dto.command.PayableCommand;
import com.yeoljeong.tripmate.payment.application.dto.command.TossConfirmCommand;
import com.yeoljeong.tripmate.payment.application.dto.result.ConfirmPaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.payment.application.properties.TossPaymentProperties;
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
    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentProperties tossPaymentProperties;

    public CreatePaymentResult createPayment(UUID userId, UUID orderId) {
        PayableCommand payableCommand = orderClient.getOrderPayment(orderId);

        validateOrderOwner(userId, payableCommand);
        validatePayableOrder(payableCommand);

        String tossOrderId = generateTossOrderId(payableCommand.orderId());

        Payment payment = Payment.create(userId, payableCommand.orderId(), tossOrderId,
                payableCommand.amount(), Instant.now());

        Payment savedPayment = paymentRepository.save(payment);

        return CreatePaymentResult.of(savedPayment, payableCommand.orderName(),
                tossPaymentProperties.successUrl(), tossPaymentProperties.failUrl());
    }

    public ConfirmPaymentResult confirmPayment(UUID userId, ConfirmPaymentCommand request) {
        Payment payment = paymentRepository.findByTossPayment_TossOrderId(request.tossOrderId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validatePaymentOwner(userId, payment);

        if (payment.isDone()) {
            return ConfirmPaymentResult.of(payment.getUserId(), payment.getOrderId(), payment.getTossPayment().getTossOrderId(),
                    payment.getTossPayment().getPaymentKey(), payment.getPaymentStatus(), payment.getPaymentMethod(),
                    payment.getPaymentAmount().getRequestedAmount(), payment.getPaymentAmount().getApprovedAmount(),
                    payment.getReceiptUrl(), payment.getPaymentTimestamps().getApprovedAt());
        }

        payment.getPaymentAmount().validateAmount(request.amount());

        try {
            TossConfirmCommand tossConfirmCommand = tossPaymentClient.confirm(request.paymentKey(), request.tossOrderId(), request.amount());

            payment.complete(tossConfirmCommand.paymentKey(), tossConfirmCommand.totalAmount(), tossConfirmCommand.method(),
                    tossConfirmCommand.approvedAt(), tossConfirmCommand.receiptUrl());
        } catch (BusinessException e) {
            payment.fail(e.getErrorCode().toString(), e.getMessage());
            throw new BusinessException(PaymentErrorCode.PAYMENT_CONFIRM_FAILED);
        }

        paymentRepository.save(payment);

        return ConfirmPaymentResult.of(payment.getUserId(), payment.getOrderId(), payment.getTossPayment().getTossOrderId(),
                payment.getTossPayment().getPaymentKey(), payment.getPaymentStatus(), payment.getPaymentMethod(),
                payment.getPaymentAmount().getRequestedAmount(), payment.getPaymentAmount().getApprovedAmount(),
                payment.getReceiptUrl(), payment.getPaymentTimestamps().getApprovedAt());
    }

    private String generateTossOrderId(UUID orderId) {
        return "ORD-" + orderId.toString().replace("-", "");
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

    private void validatePaymentOwner(UUID userId, Payment payment) {
        if (!userId.equals(payment.getUserId())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_OWNER_MISMATCH);
        }
    }
}
