package com.yeoljeong.tripmate.payment.application.service.command;

import com.yeoljeong.tripmate.event.EventUtils;
import com.yeoljeong.tripmate.event.PaymentFailedEvent;
import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.payment.application.port.PaymentOutboxRecorder;
import com.yeoljeong.tripmate.payment.domain.model.Payment;
import com.yeoljeong.tripmate.payment.domain.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PaymentFailureService {

    private final PaymentRepository paymentRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final PaymentOutboxRecorder paymentOutboxRecorder;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void fail(Payment payment, String failureCode, String failureReason) throws NoSuchAlgorithmException {
        payment.fail(failureCode, failureReason);
        Payment savedPayment = paymentRepository.save(payment);

        PaymentFailedEvent event = new PaymentFailedEvent(
                EventUtils.getEventHash("payment", savedPayment.getId().toString(), savedPayment.getUpdatedAt()),
                savedPayment.getUserId(),
                savedPayment.getOrderId(),
                savedPayment.getId(),
                savedPayment.getProductName(),
                savedPayment.getPaymentAmount().getRequestedAmount(),
                savedPayment.getFailureReason()
        );

        // 결제 실패 이벤트 outbox에 저장
        paymentOutboxRecorder.record(PaymentTopic.PAYMENT_FAILED_TOPIC, event);
    }
}
