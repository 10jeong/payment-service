package com.yeoljeong.tripmate.payment.infrastructure.messaging;

import com.yeoljeong.tripmate.event.enums.PaymentTopic;
import com.yeoljeong.tripmate.event.PaymentCompletedEvent;
import com.yeoljeong.tripmate.event.PaymentFailedEvent;
import com.yeoljeong.tripmate.payment.application.port.PaymentEventPublisher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentKafkaProducer implements PaymentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public void publish(PaymentCompletedEvent event) {
        kafkaTemplate.send(PaymentTopic.PAYMENT_COMPLETED_TOPIC,event.paymentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("payment.completed 발행 실패 - eventId={}, paymentId={}, orderId={}, productName={}, paymentPrice={}, paymentMethod={}",
                                event.eventId(), event.paymentId(), event.orderId(),
                                event.productName(), event.paymentPrice(), event.paymentMethod(), ex);
                    }
                });
    }

    @Override
    public void publish(PaymentFailedEvent event) {
        kafkaTemplate.send(PaymentTopic.PAYMENT_FAILED_TOPIC,event.paymentId().toString(), event)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("payment.failed 발행 실패 - eventId={}, paymentId={}, orderId={}, productName={}, paymentPrice={}, errorMessage={}",
                                event.eventId(), event.paymentId(), event.orderId(),
                                event.productName(), event.paymentPrice(), event.paymentErrorMessage(), ex);
                    }
                });
    }
}
