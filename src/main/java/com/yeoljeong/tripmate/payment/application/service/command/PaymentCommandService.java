package com.yeoljeong.tripmate.payment.application.service.command;

import com.yeoljeong.tripmate.event.EventUtils;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.client.OrderClient;
import com.yeoljeong.tripmate.payment.application.client.TossPaymentClient;
import com.yeoljeong.tripmate.payment.application.dto.command.ConfirmPaymentCommand;
import com.yeoljeong.tripmate.payment.application.dto.command.PayableCommand;
import com.yeoljeong.tripmate.payment.application.dto.command.TossConfirmCommand;
import com.yeoljeong.tripmate.payment.application.dto.result.ConfirmPaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.event.PaymentCompletedEvent;
import com.yeoljeong.tripmate.payment.application.properties.TossPaymentProperties;
import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
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
    private final PaymentFailureService paymentFailureService;
    private final ApplicationEventPublisher eventPublisher;

    public CreatePaymentResult createPayment(UUID userId, UUID orderId) {
        PayableCommand payableCommand = orderClient.getOrderPayment(orderId);

        validateOrderOwner(userId, payableCommand);
        validatePayableOrder(payableCommand);
        validatePaymentNotCompleted(payableCommand.orderId());

        String tossOrderId = generateTossOrderId(payableCommand.orderId());

        Payment payment = Payment.create(userId, payableCommand.orderId(),
                payableCommand.orderName(), tossOrderId, payableCommand.amount(), Instant.now());

        Payment savedPayment = paymentRepository.save(payment);

        return CreatePaymentResult.of(savedPayment, payableCommand.orderName(),
                tossPaymentProperties.successUrl(), tossPaymentProperties.failUrl());
    }

    public ConfirmPaymentResult confirmPayment(UUID userId, ConfirmPaymentCommand command) throws NoSuchAlgorithmException {
        Payment payment = paymentRepository.findByTossPayment_TossOrderId(command.tossOrderId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        validatePaymentOwner(userId, payment);

        if (payment.isDone()) {
            return ConfirmPaymentResult.of(payment.getId(), payment.getOrderId(), payment.getTossPayment().getTossOrderId(),
                    payment.getTossPayment().getPaymentKey(), payment.getPaymentStatus(), payment.getPaymentMethod(),
                    payment.getPaymentAmount().getRequestedAmount(), payment.getPaymentAmount().getApprovedAmount(),
                    payment.getReceiptUrl(), payment.getPaymentTimestamps().getApprovedAt());
        }

        if (paymentRepository.existsByOrderIdAndStatus(payment.getOrderId(), PaymentStatus.DONE)) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED);
        }

        payment.getPaymentAmount().validateAmount(command.amount());

        try {
            TossConfirmCommand tossConfirmCommand = tossPaymentClient.confirm(command.paymentKey(), command.tossOrderId(), command.amount());

            payment.complete(tossConfirmCommand.paymentKey(), tossConfirmCommand.totalAmount(), tossConfirmCommand.method(),
                    tossConfirmCommand.approvedAt(), tossConfirmCommand.receiptUrl());
        } catch (BusinessException e) {
            paymentFailureService.fail(payment, e.getErrorCode().toString(), e.getMessage());
            throw e;
        }

        Payment savedPayment = paymentRepository.save(payment);

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                EventUtils.getEventHash("payment", savedPayment.getId().toString(), savedPayment.getUpdatedAt()),
                savedPayment.getUserId(),
                savedPayment.getOrderId(),
                savedPayment.getId(),
                savedPayment.getProductName(),
                savedPayment.getPaymentAmount().getApprovedAmount(),
                savedPayment.getPaymentTimestamps().getApprovedAt(),
                savedPayment.getPaymentMethod()
        );

        // 결제 성공 이벤트 발행
        eventPublisher.publishEvent(event);

        return ConfirmPaymentResult.of(savedPayment.getId(), savedPayment.getOrderId(), savedPayment.getTossPayment().getTossOrderId(),
                savedPayment.getTossPayment().getPaymentKey(), savedPayment.getPaymentStatus(), savedPayment.getPaymentMethod(),
                savedPayment.getPaymentAmount().getRequestedAmount(), savedPayment.getPaymentAmount().getApprovedAmount(),
                savedPayment.getReceiptUrl(), savedPayment.getPaymentTimestamps().getApprovedAt());
    }

    private String generateTossOrderId(UUID orderId) {
        return "ORD-" + orderId.toString().replace("-", "")
                + "-" + UUID.randomUUID().toString().replace("-", "").substring(0, 8);
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

    private void validatePaymentNotCompleted(UUID orderId) {
        if (paymentRepository.existsByOrderIdAndStatus(orderId, PaymentStatus.DONE)) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_ALREADY_COMPLETED);
        }
    }

    private void validatePaymentOwner(UUID userId, Payment payment) {
        if (!userId.equals(payment.getUserId())) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_OWNER_MISMATCH);
        }
    }
}
