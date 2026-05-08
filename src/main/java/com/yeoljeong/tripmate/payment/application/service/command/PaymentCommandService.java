package com.yeoljeong.tripmate.payment.application.service.command;

import com.yeoljeong.tripmate.event.EventUtils;
import com.yeoljeong.tripmate.event.PaymentCompletedEvent;
import com.yeoljeong.tripmate.event.PaymentRefundedEvent;
import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.exception.BusinessException;
import com.yeoljeong.tripmate.payment.application.client.OrderClient;
import com.yeoljeong.tripmate.payment.application.client.TossPaymentClient;
import com.yeoljeong.tripmate.payment.application.dto.command.*;
import com.yeoljeong.tripmate.payment.application.dto.result.ConfirmPaymentResult;
import com.yeoljeong.tripmate.payment.application.dto.result.CreatePaymentResult;
import com.yeoljeong.tripmate.payment.application.exception.ExternalPaymentException;
import com.yeoljeong.tripmate.payment.application.exception.ExternalPaymentFailureReason;
import com.yeoljeong.tripmate.payment.application.port.PaymentOutboxRecorder;
import com.yeoljeong.tripmate.payment.application.properties.TossPaymentProperties;
import com.yeoljeong.tripmate.payment.domain.enums.PaymentStatus;
import com.yeoljeong.tripmate.payment.domain.exception.PaymentErrorCode;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentCommandService {

    private final PaymentRepository paymentRepository;
    private final OrderClient orderClient;
    private final TossPaymentClient tossPaymentClient;
    private final TossPaymentProperties tossPaymentProperties;
    private final PaymentFailureService paymentFailureService;
    private final PaymentOutboxRecorder paymentOutboxRecorder;

    public CreatePaymentResult createPayment(UUID userId, UUID orderId) {
        PayableCommand payableCommand = orderClient.getOrderPayment(orderId);

        Optional<Payment> readyPayment = paymentRepository.findByOrderIdAndPaymentStatus(orderId, PaymentStatus.READY);

        validateOrderOwner(userId, payableCommand);
        validatePayableOrder(payableCommand);
        validatePaymentNotCompleted(payableCommand.orderId());

        if (readyPayment.isPresent()) {
            return CreatePaymentResult.of(readyPayment.get(), payableCommand.orderName(),
                    tossPaymentProperties.successUrl(), tossPaymentProperties.failUrl());
        }

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
            validatePaymentKey(command.paymentKey(), payment.getTossPayment().getPaymentKey());

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
        } catch (ExternalPaymentException e) {

            // 이미 처리된 결제인 경우, 기존 DONE 결제 반환
            if (e.getReason() == ExternalPaymentFailureReason.ALREADY_PROCESSED) {
                return findCompletedPaymentResult(command);
            }

            paymentFailureService.fail(payment, e.getReason().name(), e.getMessage());
            throw e;
        }

        PaymentCompletedEvent event = new PaymentCompletedEvent(
                EventUtils.getEventHash("payment", payment.getId().toString(), payment.getUpdatedAt()),
                payment.getUserId(),
                payment.getOrderId(),
                payment.getId(),
                payment.getProductName(),
                payment.getPaymentAmount().getApprovedAmount(),
                payment.getPaymentTimestamps().getApprovedAt(),
                payment.getPaymentMethod()
        );

        // 결제 성공 이벤트 outbox에 저장
        paymentOutboxRecorder.record(PaymentTopic.PAYMENT_COMPLETED_TOPIC, event);

        return ConfirmPaymentResult.of(payment.getId(), payment.getOrderId(), payment.getTossPayment().getTossOrderId(),
                payment.getTossPayment().getPaymentKey(), payment.getPaymentStatus(), payment.getPaymentMethod(),
                payment.getPaymentAmount().getRequestedAmount(), payment.getPaymentAmount().getApprovedAmount(),
                payment.getReceiptUrl(), payment.getPaymentTimestamps().getApprovedAt());
    }

    private ConfirmPaymentResult findCompletedPaymentResult(ConfirmPaymentCommand command) {
        Payment completedPayment = paymentRepository.findByTossPayment_TossOrderIdAndStatus(command.tossOrderId(), PaymentStatus.DONE)
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_CONFIRM_RESULT_NOT_FOUND));

        validatePaymentKey(command.paymentKey(), completedPayment.getTossPayment().getPaymentKey());

        return ConfirmPaymentResult.of(completedPayment.getId(), completedPayment.getOrderId(), completedPayment.getTossPayment().getTossOrderId(),
                completedPayment.getTossPayment().getPaymentKey(), completedPayment.getPaymentStatus(), completedPayment.getPaymentMethod(),
                completedPayment.getPaymentAmount().getRequestedAmount(), completedPayment.getPaymentAmount().getApprovedAmount(),
                completedPayment.getReceiptUrl(), completedPayment.getPaymentTimestamps().getApprovedAt());
    }

    // 주문 취소 이벤트 수신 후 동작
    public void refundPayment(RefundPaymentCommand refundPaymentCommand) throws NoSuchAlgorithmException {
        Payment payment = paymentRepository.findByOrderIdAndUserId(refundPaymentCommand.orderId(), refundPaymentCommand.userId())
                .orElseThrow(() -> new BusinessException(PaymentErrorCode.PAYMENT_NOT_FOUND));

        if (payment.isCanceled()) {
            // TODO: 조건부 UPDATE 결과로 멱등성 처리 필요

            return;
        }

        if (!payment.isDone()) {
            throw new BusinessException(PaymentErrorCode.PAYMENT_NOT_REFUNDABLE);
        }

        try {
            // 토스 환불 API 요청
            // TODO: 일정 취소 시 취소 사유를 이벤트로 전달 받아 적용
            TossRefundCommand tossRefundCommand = tossPaymentClient.refundPayment(payment.getTossPayment().getPaymentKey(), "단순 변심");

            payment.cancel(BigDecimal.valueOf(tossRefundCommand.totalAmount()));
        } catch (ExternalPaymentException e) {
            // TODO: REFUNDING 상태 추가 후 DONE 복구 로직 필요

            log.warn("토스 환불 요청 실패. paymentId={}, tossOrderId={}", payment.getId(), payment.getTossPayment().getTossOrderId(), e);
            throw e;
        }

        PaymentRefundedEvent event = new PaymentRefundedEvent(
                EventUtils.getEventHash("payment", payment.getId().toString(), payment.getUpdatedAt()),
                payment.getUserId(),
                refundPaymentCommand.productId(),
                refundPaymentCommand.productName(),
                refundPaymentCommand.scheduleId(),
                refundPaymentCommand.quantity(),
                payment.getPaymentAmount().getCanceledAmount()
        );

        // 결제 환불 이벤트 outbox에 저장
        paymentOutboxRecorder.record(PaymentTopic.PAYMENT_REFUNDED_TOPIC, event);
    }

    private void validatePaymentKey(String commandPaymentKey, String paymentKey) {
        if (commandPaymentKey == null || !commandPaymentKey.equals(paymentKey)) {
            throw new BusinessException(PaymentErrorCode.INVALID_PAYMENT_KEY);
        }
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
