package com.yeoljeong.tripmate.payment.infrastructure.messaging.handler;

import com.yeoljeong.tripmate.event.PaymentCompletedEvent;
import com.yeoljeong.tripmate.event.PaymentFailedEvent;
import com.yeoljeong.tripmate.payment.application.port.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
@RequiredArgsConstructor
public class PaymentEventHandler {

    private final PaymentEventPublisher paymentEventPublisher;

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentCompletedEvent event) { paymentEventPublisher.publish(event); }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void handle(PaymentFailedEvent event) { paymentEventPublisher.publish(event); }
}
